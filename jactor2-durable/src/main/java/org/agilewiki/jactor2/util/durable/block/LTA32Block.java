package org.agilewiki.jactor2.util.durable.block;

import org.agilewiki.jactor2.util.durable.incDes.JALong;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;

import java.util.zip.Adler32;

/**
 * A block with a length, timestamp and Adler32 checksum in the header.
 */
public class LTA32Block extends LTBlock {
    Adler32 a32 = new Adler32();
    long checksum;

    @Override
    public int headerLength() {
        return super.headerLength() + JALong.LENGTH;
    }

    @Override
    protected void saveHeader(final AppendableBytes ab, final int l)
            throws Exception {
        super.saveHeader(ab, l);
        a32.reset();
        a32.update(blockBytes, headerLength(), blockBytes.length
                - headerLength());
        ab.writeLong(a32.getValue());
    }

    @Override
    public int setHeaderBytes(final byte[] bytes) {
        final int l = super.setHeaderBytes(bytes);
        checksum = rb.readLong();
        return l;
    }

    @Override
    public boolean setRootBytes(final byte[] bytesRead) {
        if (!super.setRootBytes(bytesRead)) {
            return false;
        }
        int i = 0;
        while (i < bytesRead.length) {
            i += 1;
        }
        a32.reset();
        a32.update(bytesRead);
        final boolean match = checksum == a32.getValue();
        if (match) {
            return true;
        }
        System.out.println("bad checksum");
        rootJidBytes = null;
        return false;
    }
}