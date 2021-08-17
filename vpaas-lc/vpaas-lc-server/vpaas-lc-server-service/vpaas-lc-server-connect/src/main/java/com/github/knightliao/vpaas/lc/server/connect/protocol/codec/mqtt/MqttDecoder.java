package com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt;

import static com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MyMqttCodeUtil.isValidClientId;
import static com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MyMqttCodeUtil.isValidMessageId;
import static com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MyMqttCodeUtil.isValidPublishTopicName;
import static com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MyMqttCodeUtil.resetUnusedFields;
import static com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MyMqttCodeUtil.validateFixedHeader;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/17 14:57
 */
public class MqttDecoder extends ReplayingDecoder<MqttDecoder.DecoderState> {

    private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;

    private boolean makeStatistic = false;

    /**
     * States of the decoder.
     * We start at READ_FIXED_HEADER, followed by
     * READ_VARIABLE_HEADER and finally READ_PAYLOAD.
     */
    enum DecoderState {
        READ_FIXED_HEADER,
        READ_VARIABLE_HEADER,
        READ_PAYLOAD,
        BAD_MESSAGE,
    }

    private MqttFixedHeader mqttFixedHeader;
    private Object variableHeader;
    private int bytesRemainingInVariablePart;

    private final int maxBytesInMessage;

    public MqttDecoder() {
        this(DEFAULT_MAX_BYTES_IN_MESSAGE);
    }

    public MqttDecoder(int maxBytesInMessage) {
        super(DecoderState.READ_FIXED_HEADER);
        this.maxBytesInMessage = maxBytesInMessage;
    }

    public MqttDecoder(boolean makeStatistic) {
        this(DEFAULT_MAX_BYTES_IN_MESSAGE);
        this.makeStatistic = makeStatistic;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        switch (state()) {
            case READ_FIXED_HEADER:
                mqttFixedHeader = decodeFixedHeader(buffer);
                bytesRemainingInVariablePart = mqttFixedHeader.remainingLength();
                checkpoint(DecoderState.READ_VARIABLE_HEADER);
                // fall through

            case READ_VARIABLE_HEADER:
                try {
                    if (bytesRemainingInVariablePart > maxBytesInMessage) {
                        throw new DecoderException("too large message: " + bytesRemainingInVariablePart + " bytes");
                    }
                    final Result<?> decodedVariableHeader = decodeVariableHeader(buffer, mqttFixedHeader);
                    variableHeader = decodedVariableHeader.value;
                    bytesRemainingInVariablePart -= decodedVariableHeader.numberOfBytesConsumed;
                    checkpoint(DecoderState.READ_PAYLOAD);
                    // fall through
                } catch (Exception cause) {
                    out.add(invalidMessage(cause));
                    return;
                }

            case READ_PAYLOAD:
                try {

                    final Result<?> decodedPayload =
                            decodePayload(
                                    buffer,
                                    mqttFixedHeader.messageType(),
                                    bytesRemainingInVariablePart,
                                    variableHeader);
                    bytesRemainingInVariablePart -= decodedPayload.numberOfBytesConsumed;
                    if (bytesRemainingInVariablePart != 0) {
                        throw new DecoderException(
                                "non-zero remaining payload bytes: " +
                                        bytesRemainingInVariablePart + " (" + mqttFixedHeader.messageType() + ')');
                    }
                    checkpoint(DecoderState.READ_FIXED_HEADER);
                    MqttMessage message = MqttMessageFactory.newMessage(
                            mqttFixedHeader, variableHeader, decodedPayload.value);

                    //
                    int length = bytesRemainingInVariablePart;
                    makeStatisticSize(ctx, length);

                    mqttFixedHeader = null;
                    variableHeader = null;
                    out.add(message);
                    break;
                } catch (Exception cause) {
                    out.add(invalidMessage(cause));
                    return;
                }

            case BAD_MESSAGE:
                // Keep discarding until disconnection.
                buffer.skipBytes(actualReadableBytes());
                break;

            default:
                // Shouldn't reach here.
                throw new Error();
        }
    }

    private void makeStatisticSize(ChannelHandlerContext ctx, int length) {

        if (makeStatistic && mqttFixedHeader != null) {

        }
    }

    private MqttMessage invalidMessage(Throwable cause) {
        checkpoint(DecoderState.BAD_MESSAGE);
        return MqttMessageFactory.newInvalidMessage(cause);
    }

    /**
     * Decodes the fixed header. It's one byte for the flags and then variable bytes for the remaining length.
     *
     * @param buffer the buffer to decode from
     * @return the fixed header
     */
    private static MqttFixedHeader decodeFixedHeader(ByteBuf buffer) {
        short b1 = buffer.readUnsignedByte();

        MqttMessageType messageType = MqttMessageType.valueOf(b1 >> 4);
        boolean dupFlag = (b1 & 0x08) == 0x08;
        int qosLevel = (b1 & 0x06) >> 1;
        boolean retain = (b1 & 0x01) != 0;

        int remainingLength = 0;
        int multiplier = 1;
        short digit;
        int loops = 0;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            loops++;
        } while ((digit & 128) != 0 && loops < 4);

        // MQTT protocol limits Remaining Length to 4 bytes
        if (loops == 4 && (digit & 128) != 0) {
            throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
        }
        MqttFixedHeader decodedFixedHeader =
                new MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, remainingLength);
        return validateFixedHeader(resetUnusedFields(decodedFixedHeader));
    }

    /**
     * Decodes the variable header (if any)
     *
     * @param buffer          the buffer to decode from
     * @param mqttFixedHeader MqttFixedHeader of the same message
     * @return the variable header
     */
    private static Result<?> decodeVariableHeader(ByteBuf buffer, MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT:
                return decodeConnectionVariableHeader(buffer);

            case CONNACK:
                return decodeConnAckVariableHeader(buffer);

            case SUBSCRIBE:
            case UNSUBSCRIBE:
            case SUBACK:
            case UNSUBACK:
            case PUBACK:
            case PUBREC:
            case PUBCOMP:
            case PUBREL:
                return decodeMessageIdVariableHeader(buffer);

            case PUBLISH:
                return decodePublishVariableHeader(buffer, mqttFixedHeader);

            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
                // Empty variable header
                return new Result<Object>(null, 0);
        }
        return new Result<Object>(null, 0); //should never reach here
    }

    private static Result<MqttConnectVariableHeader> decodeConnectionVariableHeader(ByteBuf buffer) {
        final Result<String> protoString = decodeString(buffer);
        int numberOfBytesConsumed = protoString.numberOfBytesConsumed;

        final byte protocolLevel = buffer.readByte();
        numberOfBytesConsumed += 1;

        final MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(protoString.value, protocolLevel);

        final int b1 = buffer.readUnsignedByte();
        numberOfBytesConsumed += 1;

        final Result<Integer> keepAlive = decodeMsbLsb(buffer);
        numberOfBytesConsumed += keepAlive.numberOfBytesConsumed;

        final boolean hasUserName = (b1 & 0x80) == 0x80;
        final boolean hasPassword = (b1 & 0x40) == 0x40;
        final boolean willRetain = (b1 & 0x20) == 0x20;
        final int willQos = (b1 & 0x18) >> 3;
        final boolean willFlag = (b1 & 0x04) == 0x04;
        final boolean cleanSession = (b1 & 0x02) == 0x02;
        if (mqttVersion == MqttVersion.MQTT_3_1_1) {
            final boolean zeroReservedFlag = (b1 & 0x01) == 0x0;
            if (!zeroReservedFlag) {
                // MQTT v3.1.1: The Server MUST validate that the reserved flag in the CONNECT Control Packet is
                // set to zero and disconnect the Client if it is not zero.
                // See http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349230
                throw new DecoderException("non-zero reserved flag");
            }
        }

        final MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                mqttVersion.protocolName(),
                mqttVersion.protocolLevel(),
                hasUserName,
                hasPassword,
                willRetain,
                willQos,
                willFlag,
                cleanSession,
                keepAlive.value);
        return new Result<MqttConnectVariableHeader>(mqttConnectVariableHeader, numberOfBytesConsumed);
    }

    private static Result<MqttConnAckVariableHeader> decodeConnAckVariableHeader(ByteBuf buffer) {
        final boolean sessionPresent = (buffer.readUnsignedByte() & 0x01) == 0x01;
        byte returnCode = buffer.readByte();
        final int numberOfBytesConsumed = 2;
        final MqttConnAckVariableHeader mqttConnAckVariableHeader =
                new MqttConnAckVariableHeader(MqttConnectReturnCode.valueOf(returnCode), sessionPresent);
        return new Result<MqttConnAckVariableHeader>(mqttConnAckVariableHeader, numberOfBytesConsumed);
    }

    private static Result<MqttMessageIdVariableHeader> decodeMessageIdVariableHeader(ByteBuf buffer) {
        final Result<Integer> messageId = decodeMessageId(buffer);
        return new Result<MqttMessageIdVariableHeader>(
                MqttMessageIdVariableHeader.from(messageId.value),
                messageId.numberOfBytesConsumed);
    }

    private static Result<MqttPublishVariableHeader> decodePublishVariableHeader(
            ByteBuf buffer,
            MqttFixedHeader mqttFixedHeader) {
        final Result<String> decodedTopic = decodeString(buffer);
        if (!isValidPublishTopicName(decodedTopic.value)) {
            throw new DecoderException("invalid publish topic name: " + decodedTopic.value + " (contains wildcards)");
        }
        int numberOfBytesConsumed = decodedTopic.numberOfBytesConsumed;

        int messageId = -1;
        if (mqttFixedHeader.qosLevel().value() > 0) {
            final Result<Integer> decodedMessageId = decodeMessageId(buffer);
            messageId = decodedMessageId.value;
            numberOfBytesConsumed += decodedMessageId.numberOfBytesConsumed;
        }
        final MqttPublishVariableHeader mqttPublishVariableHeader =
                new MqttPublishVariableHeader(decodedTopic.value, messageId);
        return new Result<MqttPublishVariableHeader>(mqttPublishVariableHeader, numberOfBytesConsumed);
    }

    private static Result<Integer> decodeMessageId(ByteBuf buffer) {
        final Result<Integer> messageId = decodeMsbLsb(buffer);
        if (!isValidMessageId(messageId.value)) {
            throw new DecoderException("invalid messageId: " + messageId.value);
        }
        return messageId;
    }

    /**
     * Decodes the payload.
     *
     * @param buffer                       the buffer to decode from
     * @param messageType                  type of the message being decoded
     * @param bytesRemainingInVariablePart bytes remaining
     * @param variableHeader               variable header of the same message
     * @return the payload
     */
    private static Result<?> decodePayload(
            ByteBuf buffer,
            MqttMessageType messageType,
            int bytesRemainingInVariablePart,
            Object variableHeader) {
        switch (messageType) {
            case CONNECT:
                return decodeConnectionPayload(buffer, (MqttConnectVariableHeader) variableHeader);

            case SUBSCRIBE:
                return decodeSubscribePayload(buffer, bytesRemainingInVariablePart);

            case SUBACK:
                return decodeSubackPayload(buffer, bytesRemainingInVariablePart);

            case UNSUBSCRIBE:
                return decodeUnsubscribePayload(buffer, bytesRemainingInVariablePart);

            case PUBLISH:
                return decodePublishPayload(buffer, bytesRemainingInVariablePart);

            default:
                // unknown payload , no byte consumed
                return new Result<Object>(null, 0);
        }
    }

    private static Result<MqttConnectPayload> decodeConnectionPayload(
            ByteBuf buffer,
            MqttConnectVariableHeader mqttConnectVariableHeader) {
        final Result<String> decodedClientId = decodeString(buffer);
        final String decodedClientIdValue = decodedClientId.value;
        final MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(mqttConnectVariableHeader.name(),
                (byte) mqttConnectVariableHeader.version());
        if (!isValidClientId(mqttVersion, decodedClientIdValue)) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + decodedClientIdValue);
        }
        int numberOfBytesConsumed = decodedClientId.numberOfBytesConsumed;

        Result<String> decodedWillTopic = null;
        Result<String> decodedWillMessage = null;
        if (mqttConnectVariableHeader.isWillFlag()) {
            decodedWillTopic = decodeString(buffer, 0, 32767);
            numberOfBytesConsumed += decodedWillTopic.numberOfBytesConsumed;
            decodedWillMessage = decodeAsciiString(buffer);
            numberOfBytesConsumed += decodedWillMessage.numberOfBytesConsumed;
        }
        Result<String> decodedUserName = null;
        Result<String> decodedPassword = null;
        if (mqttConnectVariableHeader.hasUserName()) {
            decodedUserName = decodeString(buffer);
            numberOfBytesConsumed += decodedUserName.numberOfBytesConsumed;
        }
        if (mqttConnectVariableHeader.hasPassword()) {
            decodedPassword = decodeString(buffer);
            numberOfBytesConsumed += decodedPassword.numberOfBytesConsumed;
        }

        final MqttConnectPayload mqttConnectPayload =
                new MqttConnectPayload(
                        decodedClientId.value,
                        decodedWillTopic != null ? decodedWillTopic.value : null,
                        decodedWillMessage != null ? decodedWillMessage.value : null,
                        decodedUserName != null ? decodedUserName.value : null,
                        decodedPassword != null ? decodedPassword.value : null);
        return new Result<MqttConnectPayload>(mqttConnectPayload, numberOfBytesConsumed);
    }

    private static Result<MqttSubscribePayload> decodeSubscribePayload(
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<MqttTopicSubscription> subscribeTopics = new ArrayList<MqttTopicSubscription>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final Result<String> decodedTopicName = decodeString(buffer);
            numberOfBytesConsumed += decodedTopicName.numberOfBytesConsumed;
            int qos = buffer.readUnsignedByte() & 0x03;
            numberOfBytesConsumed++;
            subscribeTopics.add(new MqttTopicSubscription(decodedTopicName.value, MqttQoS.valueOf(qos)));
        }
        return new Result<MqttSubscribePayload>(new MqttSubscribePayload(subscribeTopics), numberOfBytesConsumed);
    }

    private static Result<MqttSubAckPayload> decodeSubackPayload(
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<Integer> grantedQos = new ArrayList<Integer>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            int qos = buffer.readUnsignedByte() & 0x03;
            numberOfBytesConsumed++;
            grantedQos.add(qos);
        }
        return new Result<MqttSubAckPayload>(new MqttSubAckPayload(grantedQos), numberOfBytesConsumed);
    }

    private static Result<MqttUnsubscribePayload> decodeUnsubscribePayload(
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<String> unsubscribeTopics = new ArrayList<String>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final Result<String> decodedTopicName = decodeString(buffer);
            numberOfBytesConsumed += decodedTopicName.numberOfBytesConsumed;
            unsubscribeTopics.add(decodedTopicName.value);
        }
        return new Result<MqttUnsubscribePayload>(
                new MqttUnsubscribePayload(unsubscribeTopics),
                numberOfBytesConsumed);
    }

    private static Result<ByteBuf> decodePublishPayload(ByteBuf buffer, int bytesRemainingInVariablePart) {
        ByteBuf b = buffer.readRetainedSlice(bytesRemainingInVariablePart);
        return new Result<ByteBuf>(b, bytesRemainingInVariablePart);
    }

    private static Result<String> decodeString(ByteBuf buffer) {
        return decodeString(buffer, 0, Integer.MAX_VALUE);
    }

    private static Result<String> decodeAsciiString(ByteBuf buffer) {
        Result<String> result = decodeString(buffer, 0, Integer.MAX_VALUE);
        final String s = result.value;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 127) {
                return new Result<String>(null, result.numberOfBytesConsumed);
            }
        }
        return new Result<String>(s, result.numberOfBytesConsumed);
    }

    private static Result<String> decodeString(ByteBuf buffer, int minBytes, int maxBytes) {
        final Result<Integer> decodedSize = decodeMsbLsb(buffer);
        int size = decodedSize.value;
        int numberOfBytesConsumed = decodedSize.numberOfBytesConsumed;
        if (size < minBytes || size > maxBytes) {
            buffer.skipBytes(size);
            numberOfBytesConsumed += size;
            return new Result<String>(null, numberOfBytesConsumed);
        }
        String s = buffer.toString(buffer.readerIndex(), size, CharsetUtil.UTF_8);
        buffer.skipBytes(size);
        numberOfBytesConsumed += size;
        return new Result<String>(s, numberOfBytesConsumed);
    }

    private static Result<Integer> decodeMsbLsb(ByteBuf buffer) {
        return decodeMsbLsb(buffer, 0, 65535);
    }

    private static Result<Integer> decodeMsbLsb(ByteBuf buffer, int min, int max) {
        short msbSize = buffer.readUnsignedByte();
        short lsbSize = buffer.readUnsignedByte();
        final int numberOfBytesConsumed = 2;
        int result = msbSize << 8 | lsbSize;
        if (result < min || result > max) {
            result = -1;
        }
        return new Result<Integer>(result, numberOfBytesConsumed);
    }

    private static final class Result<T> {

        private final T value;
        private final int numberOfBytesConsumed;

        Result(T value, int numberOfBytesConsumed) {
            this.value = value;
            this.numberOfBytesConsumed = numberOfBytesConsumed;
        }
    }
}
