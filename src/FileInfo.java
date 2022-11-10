import java.util.ArrayList;
import java.nio.ByteBuffer;

/**
 * Class for getting and storing file data blocks.
 */
public class FileInfo
{
    private final Volume vol;
    private final ArrayList<DataBlock> dataBlocks = new ArrayList<DataBlock>();
    private final Inode inode;
    private int truesize = 0;
    private int currentBlock = -1;
    private int numBlocks = 0;
    private int numUsefulBlocks = 0;

    /**
     * Gets and saves file data blocks
     * @param inode File inode
     * @param vol Volume
     */
    public FileInfo(Inode inode, Volume vol)
    {
        this.inode = inode;
        this.vol = vol;

        for(long pointer :inode.getBlockpointer())
        {
            if(numBlocks * Constants.BLOCK_SIZE>=inode.getSize())
                break;

            if(pointer !=0)
                dataBlocks.add(new DataBlock(numBlocks,pointer));

            numBlocks++;
        }

        indirectTraverse(inode.getIndirectPointer());

        doubleIndirectTraverse(inode.getDoubleIndirectPointer());

        tripleIndirectTraverse(inode.getTripleIndirectPointer());

        truesize = dataBlocks.size() * Constants.BLOCK_SIZE;
        numUsefulBlocks = dataBlocks.size();
    }

    /**
     * Gets indirect pointer data blocks
     * @param Indirectpointer Indirect pointer
     */
    private void indirectTraverse(long Indirectpointer)
    {
        if(Indirectpointer!=0)
        {
            ByteBuffer bf = vol.getByteBuffer(Indirectpointer*Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
            int pointer;
            
            while(bf.hasRemaining() && (numBlocks * Constants.BLOCK_SIZE)<inode.getSize())
            {
                pointer = bf.getInt();
                if(pointer !=0)
                    dataBlocks.add(new DataBlock(numBlocks,pointer));

                numBlocks++;
            }
        }
        else if(numBlocks * Constants.BLOCK_SIZE>=inode.getSize());
        else
            numBlocks += Constants.INODES_PER_BLOCK;
    }

    /**
     * Gets double indirect pointer data blocks
     * @param Indirectpointer Double indirect pointer
     */
    private void doubleIndirectTraverse(long Indirectpointer)
    {
        
        if(Indirectpointer!=0)
        {
            ByteBuffer bf = vol.getByteBuffer(Indirectpointer*Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
            int pointer;

            while(bf.hasRemaining())
            {
                pointer = bf.getInt();
                indirectTraverse(pointer);
            }
        }
        else if(numBlocks * Constants.BLOCK_SIZE>=inode.getSize());
        else
            numBlocks += Constants.INODES_PER_BLOCK * Constants.INODES_PER_BLOCK;
    }

    /**
     * Gets triple indirect pointer data blocks
     * @param Indirectpointer Triple indirect pointer
     */
    private void tripleIndirectTraverse(long Indirectpointer)
    {
        if(Indirectpointer!=0)
        {
            ByteBuffer bf = vol.getByteBuffer(Indirectpointer*Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
            int pointer;

            while(bf.hasRemaining())
            {
                pointer = bf.getInt();
                doubleIndirectTraverse(pointer);
            }
        }
        else if(numBlocks * Constants.BLOCK_SIZE>=inode.getSize());
        else
            numBlocks += Constants.INODES_PER_BLOCK * Constants.INODES_PER_BLOCK * Constants.INODES_PER_BLOCK;
    }
    
    /**
     * Gets data block at index no from data block list
     * @param no Block index in block list
     * @return Data block at index no from data block list
     */
    public ByteBuffer getBlock(int no)
    {
        currentBlock = no;
        long pointer = returnBlockPointer(no);

        if(pointer == 0)
            return vol.wrapBuffer(new byte[Constants.BLOCK_SIZE]);
        else
            return vol.getByteBuffer(pointer * Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
    }

    /**
     * Gets data block at current position and increments the position by one
     * @return Data block at current position
     */
    public ByteBuffer getNextBlock()
    {
        currentBlock++;
        long pointer = returnBlockPointer(currentBlock);

        if(pointer == 0)
            return vol.wrapBuffer(new byte[Constants.BLOCK_SIZE]);
        else
            return vol.getByteBuffer(pointer * Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
    }

    /**
     * Gets non-empty data block at current position and increments position by one
     * @return Non-empty data block at current position
     */
    public ByteBuffer getNextUsefulBlock()
    {
        currentBlock++;

        if(dataBlocks.size()>currentBlock)
            return vol.getByteBuffer(dataBlocks.get(currentBlock).block_pointer * Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
        else
            return vol.wrapBuffer(new byte[Constants.BLOCK_SIZE]);
    }
    
    /**
     * Gets the true size of the file (non-empty blocks)
     * @return True size of the file
     */
    public int getTrueSize()
    {
        return truesize;
    }

    /**
     * Gets current block position
     * @return Current block position
     */
    public int getCurrentBlock()
    {
        return currentBlock;
    }

    /**
     * Gets amount of data blocks in file
     * @return Amount of data blocks in file
     */
    public int getBlockNo()
    {
        return numBlocks;
    }

    /**
     * Resets current block position
     */
    public void resetCurrentBlock()
    {
        currentBlock = -1;
    }

    /**
     * Gets number of non-empty data blocks
     * @return Number of non-empty data blocks
     */
    public int getNumUsefulBlocks()
    {
        return numUsefulBlocks;
    }

    /**
     * Gets block pointer from dataBlock wtih specified block number
     * @param block_number Block number
     * @return Block pointer from dataBlock
     */
    private long returnBlockPointer(long block_number)
    {
        for(DataBlock dataBlock : dataBlocks)
        {
            if(dataBlock.block_number == block_number)
                return dataBlock.block_pointer;
        }
        return 0;
    }

    /**
     * Inner class to save non-empty data blocks
     * No getters/setters needed, since this is a private Inner class 
     */
    private class DataBlock
    {
        private long block_number;
        private long block_pointer;
        
        /**
         * Create DataBlock
         * @param block_number Block number
         * @param block_pointer Block pointer
         */
        private DataBlock(long block_number, long block_pointer)
        {
            this.block_number = block_number;
            this.block_pointer = block_pointer;
        }
    }
}