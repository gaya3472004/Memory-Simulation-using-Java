import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private final int totalSize;
    private final List<MemoryBlock> allocatedBlocks = new ArrayList<>();
    private final List<MemoryBlock> freeBlocks = new ArrayList<>();

    public MemoryManager(int size) {
        this.totalSize = size;
        this.freeBlocks.add(new MemoryBlock(-1, size, 0)); // Initial free block
    }

    public boolean allocateMemory(int processId, int size, String strategy) {
        if (size <= 0 || processId <= 0) {
            return false; // Invalid input
        }

        // Check if there's enough free memory available
        for (MemoryBlock block : freeBlocks) {
            if (block.size >= size) {
                // Allocate memory based on strategy
                switch (strategy.toLowerCase()) {
                    case "first":
                        return allocateFirstFit(processId, size, block);
                    case "best":
                        return allocateBestFit(processId, size);
                    case "worst":
                        return allocateWorstFit(processId, size);
                    default:
                        return false; // Invalid strategy
                }
            }
        }
        return false; // Not enough free memory
    }

    private boolean allocateFirstFit(int processId, int size, MemoryBlock block) {
        int start = block.address;
        if (size <= block.size) {
            allocatedBlocks.add(new MemoryBlock(processId, size, start));
            block.size -= size;
            block.address += size;
            if (block.size == 0) {
                freeBlocks.remove(block);
            }
            return true;
        }
        return false;
    }

    private boolean allocateBestFit(int processId, int size) {
        MemoryBlock bestBlock = null;
        for (MemoryBlock block : freeBlocks) {
            if (block.size >= size && (bestBlock == null || block.size < bestBlock.size)) {
                bestBlock = block;
            }
        }
        if (bestBlock != null) {
            return allocateFirstFit(processId, size, bestBlock);
        }
        return false;
    }

    private boolean allocateWorstFit(int processId, int size) {
        MemoryBlock worstBlock = null;
        for (MemoryBlock block : freeBlocks) {
            if (block.size >= size && (worstBlock == null || block.size > worstBlock.size)) {
                worstBlock = block;
            }
        }
        if (worstBlock != null) {
            return allocateFirstFit(processId, size, worstBlock);
        }
        return false;
    }

    public void deallocateMemory(int processId) {
        List<MemoryBlock> toRemove = new ArrayList<>();
        for (MemoryBlock block : allocatedBlocks) {
            if (block.processId == processId) {
                freeBlocks.add(new MemoryBlock(-1, block.size, block.address));
                toRemove.add(block);
            }
        }
        allocatedBlocks.removeAll(toRemove);
        mergeFreeBlocks();
    }

    private void mergeFreeBlocks() {
        freeBlocks.sort((a, b) -> Integer.compare(a.address, b.address));
        List<MemoryBlock> mergedBlocks = new ArrayList<>();
        MemoryBlock current = null;
        for (MemoryBlock block : freeBlocks) {
            if (current == null) {
                current = block;
            } else if (current.address + current.size == block.address) {
                current.size += block.size;
            } else {
                mergedBlocks.add(current);
                current = block;
            }
        }
        if (current != null) {
            mergedBlocks.add(current);
        }
        freeBlocks.clear();
        freeBlocks.addAll(mergedBlocks);
    }

    public String getMemoryStatus() {
        int allocatedSpace = 0;
        int freeSpace = totalSize;

        // Calculate allocated space
        for (MemoryBlock block : allocatedBlocks) {
            allocatedSpace += block.size;
            freeSpace -= block.size;
        }

        // Format output
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<b>Initialized Space:</b> ").append(totalSize).append(" units<br>");
        sb.append("<b>Allocated Blocks:</b><br>");
        for (MemoryBlock block : allocatedBlocks) {
            sb.append(String.format("Process ID %d: %d units at address %d<br>",
                    block.processId, block.size, block.address));
        }
        sb.append("<br><b>Free Space:</b> ").append(freeSpace).append(" units<br>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private static class MemoryBlock {
        int processId;
        int size;
        int address;

        MemoryBlock(int processId, int size, int address) {
            this.processId = processId;
            this.size = size;
            this.address = address;
        }
    }
}
