package com.pg85.otg.gen.resource;

import com.pg85.otg.constants.Constants;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.helpers.RandomHelper;
import com.pg85.otg.util.interfaces.IBiomeConfig;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class CactusResource extends FrequencyResourceBase
{
	private final LocalMaterialData material;
    private final int minAltitude;
    private final int maxAltitude;
    private final MaterialSet sourceBlocks;

    public CactusResource(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
    {
        super(biomeConfig, args, logger, materialReader);
        assureSize(6, args);

        this.material = materialReader.readMaterial(args.get(0));
        this.frequency = readInt(args.get(1), 1, 100);
        this.rarity = readRarity(args.get(2));
        this.minAltitude = readInt(args.get(3), Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1);
        this.maxAltitude = readInt(args.get(4), this.minAltitude, Constants.WORLD_HEIGHT - 1);
        this.sourceBlocks = readMaterials(args, 5, materialReader);
    }

    @Override
    public void spawn(IWorldGenRegion worldGenregion, Random rand, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
        int y = RandomHelper.numberInRange(rand, this.minAltitude, this.maxAltitude);        
        LocalMaterialData worldMaterial;
        int cactusX;
        int cactusBaseY;
        int cactusZ;
        int cactusHeight;
        for (int i = 0; i < 10; i++)
        {
            cactusX = x + rand.nextInt(8) - rand.nextInt(8);
            cactusBaseY = y + rand.nextInt(4) - rand.nextInt(4);
            cactusZ = z + rand.nextInt(8) - rand.nextInt(8);

            worldMaterial = worldGenregion.getMaterial(cactusX, cactusBaseY, cactusZ,  chunkBeingPopulated);
            if(worldMaterial == null || !worldMaterial.isAir())
            {
            	continue;
            }
            
            // Check foundation
            worldMaterial = worldGenregion.getMaterial(cactusX, cactusBaseY - 1, cactusZ, chunkBeingPopulated);
            if (worldMaterial == null || !this.sourceBlocks.contains(worldMaterial))
            {
                continue;
            }

            // Check neighbors
            worldMaterial = worldGenregion.getMaterial(cactusX - 1, cactusBaseY, cactusZ, chunkBeingPopulated);
            if (worldMaterial == null || !worldMaterial.isAir())
            {
                continue;
            }
            
            worldMaterial = worldGenregion.getMaterial(cactusX + 1, cactusBaseY, cactusZ, chunkBeingPopulated);
            if (worldMaterial == null || !worldMaterial.isAir())
            {
                continue;
            }
            
            worldMaterial = worldGenregion.getMaterial(cactusX, cactusBaseY, cactusZ - 1, chunkBeingPopulated);
            if (worldMaterial == null || !worldMaterial.isAir())
            {
                continue;
            }
            
            worldMaterial = worldGenregion.getMaterial(cactusX, cactusBaseY, cactusZ + 1, chunkBeingPopulated);
            if (worldMaterial == null || !worldMaterial.isAir())
            {
                continue;
            }

            // Spawn cactus
            cactusHeight = 1 + rand.nextInt(rand.nextInt(3) + 1);
            for (int dY = 0; dY < cactusHeight; dY++)
            {
            	worldGenregion.setBlock(cactusX, cactusBaseY + dY, cactusZ, this.material, null, chunkBeingPopulated, false);
            }
        }
    }

    @Override
    public String toString()
    {
        return "Cactus(" + this.material + "," + this.frequency + "," + this.rarity + "," + this.minAltitude + "," + this.maxAltitude + makeMaterials(this.sourceBlocks) + ")";
    }
}