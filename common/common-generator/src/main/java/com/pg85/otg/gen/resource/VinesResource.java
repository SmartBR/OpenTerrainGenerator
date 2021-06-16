package com.pg85.otg.gen.resource;

import com.pg85.otg.constants.Constants;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.interfaces.IBiomeConfig;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;

import java.util.List;
import java.util.Random;

public class VinesResource extends FrequencyResourceBase
{
    private static final LocalMaterialData[] FROM_DIRECTION =
    {
        LocalMaterials.VINE_SOUTH, 
        LocalMaterials.VINE_NORTH, 
        LocalMaterials.VINE_EAST, 
        LocalMaterials.VINE_WEST
    };

    private final int maxAltitude;
    private final int minAltitude;

    public VinesResource(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
    {
    	super(biomeConfig, args, logger, materialReader);

        assureSize(4, args);
        this.frequency = readInt(args.get(0), 1, 100);
        this.rarity = readRarity(args.get(1));
        this.minAltitude = readInt(args.get(2), Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1);
        this.maxAltitude = readInt(args.get(3), this.minAltitude, Constants.WORLD_HEIGHT - 1);
    }
    
    private boolean canPlace(IWorldGenRegion worldGenRegion, int x, int y, int z, int direction, ChunkCoordinate chunkBeingPopulated)
    {
        LocalMaterialData sourceBlock;
        switch (direction)
        {
            default:
                return false;
            case 1:
                sourceBlock = worldGenRegion.getMaterial(x, y + 1, z, chunkBeingPopulated);
                break;
            case 2:
                sourceBlock = worldGenRegion.getMaterial(x, y, z + 1, chunkBeingPopulated);
                break;
            case 3:
                sourceBlock = worldGenRegion.getMaterial(x, y, z - 1, chunkBeingPopulated);
                break;
            case 5:
                sourceBlock = worldGenRegion.getMaterial(x - 1, y, z, chunkBeingPopulated);
                break;
            case 4:
                sourceBlock = worldGenRegion.getMaterial(x + 1, y, z, chunkBeingPopulated);
                break;
        }
        return sourceBlock != null && sourceBlock.isSolid();
    }

    @Override
    public void spawn(IWorldGenRegion worldGenRegion, Random rand, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
        int _x = x;
        int _z = z;
        int y = this.minAltitude;

        LocalMaterialData worldMaterial;
        
        while (y <= this.maxAltitude)
        {
        	worldMaterial = worldGenRegion.getMaterial(_x, y, _z, chunkBeingPopulated);
            if (worldMaterial != null && worldMaterial.isAir())
            {
                // TODO: Refactor to enum
                for (int direction = 2; direction <= 5; direction++)
                {
                    if (canPlace(worldGenRegion, _x, y, _z, direction, chunkBeingPopulated))
                    {
                    	worldGenRegion.setBlock(_x, y, _z, FROM_DIRECTION[direction - 2], null, chunkBeingPopulated, false);
                        break;
                    }
                }
            } else {
                _x = x + rand.nextInt(4) - rand.nextInt(4);
                _z = z + rand.nextInt(4) - rand.nextInt(4);
            }
            y++;
        }
    }
    
    @Override
    public String toString()
    {
        return "Vines(" + this.frequency + "," + this.rarity + "," + this.minAltitude + "," + this.maxAltitude + ")";
    }    
}