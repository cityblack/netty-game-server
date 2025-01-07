package com.lzh.game.framework.utils.id;

/**
 * Unsafe
 *
 * Distributed Sequence Generator.
 * Inspired by Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 *
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of SequenceGenerator per node in your distributed system cluster.
 */
/**
 * Snowflake ID Generator
 *
 * Generates unique distributed IDs with the following bit allocation:
 * - 1 bit: Sign bit (always 0)
 * - 41 bits: Timestamp (milliseconds since custom epoch)
 * - 10 bits: Machine/Worker ID
 * - 12 bits: Sequence number
 */
public class SnowflakeIdGenerator implements IdGenerator {
    // Configuration constants
    private static final long CUSTOM_EPOCH = 1609459200000L; // Jan 1, 2021
    private static final int WORKER_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    // Bit shift positions
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // Maximum values for worker ID and sequence
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // Instance variables
    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    /**
     * Constructor for SnowflakeIdGenerator
     *
     * @param workerId Unique identifier for this worker/machine (0-1023)
     */
    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format(
                    "Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    /**
     * Generate a unique Snowflake ID
     *
     * @return Unique 64-bit long ID
     */
    @Override
    public long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        // Handle clock drift or system time changes
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        // If same millisecond, increment sequence
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // If sequence overflows, wait for next millisecond
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // Reset sequence for new timestamp
            sequence = 0L;
        }

        // Update last timestamp
        lastTimestamp = currentTimestamp;

        // Construct the unique ID
        return ((currentTimestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    /**
     * Get current timestamp in milliseconds
     *
     * @return Current timestamp
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Wait for next millisecond if current timestamp is the same
     *
     * @param currentTimestamp Current timestamp
     * @return Next unique timestamp
     */
    private long waitNextMillis(long currentTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= currentTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * Extract components from a Snowflake ID
     *
     * @param id Snowflake ID
     * @return Breakdown of ID components
     */
    public static IdComponents parseId(long id) {
        long timestamp = ((id >> TIMESTAMP_SHIFT) & ((1L << 41) - 1)) + CUSTOM_EPOCH;
        long workerId = (id >> WORKER_ID_SHIFT) & ((1L << WORKER_ID_BITS) - 1);
        long sequence = id & ((1L << SEQUENCE_BITS) - 1);

        return new IdComponents(timestamp, workerId, sequence);
    }

    /**
     * Represents components of a Snowflake ID
     */
    public static class IdComponents {
        public final long timestamp;
        public final long workerId;
        public final long sequence;

        public IdComponents(long timestamp, long workerId, long sequence) {
            this.timestamp = timestamp;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        @Override
        public String toString() {
            return String.format(
                    "Timestamp: %d, Worker ID: %d, Sequence: %d",
                    timestamp, workerId, sequence
            );
        }
    }

}
