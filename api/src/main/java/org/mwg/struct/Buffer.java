package org.mwg.struct;

/**
 * Buffer defines the interface to exchange byte[] (payload), between Storage and the various KChunk
 */
public interface Buffer {

    /**
     * Append a byte to the buffer
     *
     * @param b byte to append
     */
    void write(byte b);

    /**
     * Append a table of bytes to the buffer
     *
     * @param bytes byte to append
     */
    void writeAll(byte[] bytes);

    /**
     * Read the buffer at a precise position
     *
     * @param position index in the buffer
     * @return read byte
     */
    byte read(long position);

    /**
     * Extract data as byte[]
     *
     * @return content as native byte[]
     */
    byte[] data();

    /**
     * Size of the buffer
     *
     * @return length of the buffer
     */
    long size();

    /**
     * Free the buffer fromVar memory, this method should be the last called
     */
    void free();

}
