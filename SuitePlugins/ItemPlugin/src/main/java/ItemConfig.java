import java.lang.reflect.Field;
import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import suite.annotation.MeshIdentifier;
import suite.annotation.OrderType;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;

/**
 * @author ReverendDread Sep 17, 2019
 */
public class ItemConfig extends ConfigExtensionBase {

	private String opcode9 = "null";
	private int category = -1;

	@Override
	public void decode(int opcode, InputStream buffer) {
		if (opcode == 1) {
			modelId = buffer.readUnsignedShort();
		} else if (opcode == 2) {
			name = buffer.readString();
		} else if (opcode == 4) {
			spriteScale = buffer.readUnsignedShort();
		} else if (opcode == 5) {
			spritePitch = buffer.readUnsignedShort();
		} else if (opcode == 6) {
			spriteCameraRoll = buffer.readUnsignedShort();
		} else if (opcode == 7) {
			spriteTranslateX = buffer.readUnsignedShort();
			if (spriteTranslateX > 32767) {
				spriteTranslateX -= 65536;
			}
		} else if (opcode == 8) {
			spriteTranslateY = buffer.readUnsignedShort();
			if (spriteTranslateY > 32767) {
				spriteTranslateY -= 65536;
			}
		} else if(opcode == 9) {
			opcode9 = buffer.readString();
		} else if (opcode == 11) {
			stackable = 1;
		} else if (opcode == 12) {
			value = buffer.readInt();
		} else if (opcode == 16) {
			membersObject = true;
		} else if (opcode == 23) {
			primaryMaleModel = buffer.readUnsignedShort();
			maleTranslation = buffer.readUnsignedByte();
		} else if (opcode == 24) {
			secondaryMaleModel = buffer.readUnsignedShort();
		} else if (opcode == 25) {
			primaryFemaleModel = buffer.readUnsignedShort();
			femaleTranslation = buffer.readUnsignedByte();
		} else if (opcode == 26) {
			secondaryFemaleModel = buffer.readUnsignedShort();
		} else if (opcode >= 30 && opcode < 35) {
			groundActions[opcode - 30] = buffer.readString();
			if (groundActions[opcode - 30].equalsIgnoreCase("Hidden")) {
				groundActions[opcode - 30] = null;
			}
		} else if (opcode >= 35 && opcode < 40) {
			itemActions[opcode - 35] = buffer.readString();
		} else if (opcode == 40) {
			int var5 = buffer.readUnsignedByte();
			originalModelColors = new int[var5];
			modifiedModelColors = new int[var5];
			for (int var4 = 0; var4 < var5; ++var4) {
				originalModelColors[var4] = buffer.readUnsignedShort();
				modifiedModelColors[var4] = buffer.readUnsignedShort();
			}
		} else if (opcode == 41) {
			int var5 = buffer.readUnsignedByte();
			originalTextureColors = new int[var5];
			modifiedTextureColors = new int[var5];
			for (int var4 = 0; var4 < var5; ++var4) {
				originalTextureColors[var4] = buffer.readUnsignedShort();
				modifiedTextureColors[var4] = buffer.readUnsignedShort();
			}
		} else if (opcode == 42) {
			shiftClickIndex = buffer.readByte();
		} else if (opcode == 65) {
			searchable = true;
		} else if (opcode == 78) {
			tertiaryMaleEquipmentModel = buffer.readUnsignedShort();
		} else if (opcode == 79) {
			tertiaryFemaleEquipmentModel = buffer.readUnsignedShort();
		} else if (opcode == 90) {
			primaryMaleHeadPiece = buffer.readUnsignedShort();
		} else if (opcode == 91) {
			primaryFemaleHeadPiece = buffer.readUnsignedShort();
		} else if (opcode == 92) {
			secondaryMaleHeadPiece = buffer.readUnsignedShort();
		} else if (opcode == 93) {
			secondaryFemaleHeadPiece = buffer.readUnsignedShort();
		} else if(opcode == 94) {
			category = buffer.readUnsignedShort();
		} else if (opcode == 95) {
			spriteCameraYaw = buffer.readUnsignedShort();
		} else if (opcode == 97) {
			certID = buffer.readUnsignedShort();
		} else if (opcode == 98) {
			certTemplateID = buffer.readUnsignedShort();
		} else if (opcode >= 100 && opcode < 110) {
			if (stackIDs == null) {
				stackIDs = new int[10];
				stackAmounts = new int[10];
			}

			stackIDs[opcode - 100] = buffer.readUnsignedShort();
			stackAmounts[opcode - 100] = buffer.readUnsignedShort();
		} else if (opcode == 110) {
			groundScaleX = buffer.readUnsignedShort();
		} else if (opcode == 111) {
			groundScaleY = buffer.readUnsignedShort();
		} else if (opcode == 112) {
			groundScaleZ = buffer.readUnsignedShort();
		} else if (opcode == 113) {
			ambience = buffer.readByte();
		} else if (opcode == 114) {
			diffusion = buffer.readByte();
		} else if (opcode == 115) {
			team = buffer.readUnsignedByte();
		} else if (opcode == 139) {
			unnotedId = buffer.readUnsignedShort();
		} else if (opcode == 140) {
			notedId = buffer.readUnsignedShort();
		} else if (opcode == 148) {
			placeholderId = buffer.readUnsignedShort();
		} else if (opcode == 149) {
			placeholderTemplateId = buffer.readUnsignedShort();
		} else if (opcode == 249) {
			int length = buffer.readUnsignedByte();

			params = new HashMap<>(length);

			for (int i = 0; i < length; i++) {
				boolean isString = buffer.readUnsignedByte() == 1;
				int key = buffer.read24BitInt();
				Object value;

				if (isString) {
					value = buffer.readString();
				}

				else {
					value = buffer.readInt();
				}

				params.put(key, value);
			}
		} else {
			System.err.println("item : " + id + ", error decoding opcode : " + opcode + ", previous opcodes: " + Arrays.toString(previousOpcodes));
		}
		ArrayUtils.add(previousOpcodes, opcode);
	}

	@Override
	public OutputStream encode(OutputStream buffer) {

		if (modelId > -1) {
			buffer.writeByte(1);
			buffer.writeShort(modelId);
		}

		if (!name.equals("null")) {
			buffer.writeByte(2);
			buffer.writeString(name);
		}

		if (spriteScale != 2000) {
			buffer.writeByte(4);
			buffer.writeShort(spriteScale);
		}

		if (spritePitch != 0) {
			buffer.writeByte(5);
			buffer.writeShort(spritePitch);
		}

		if (spriteCameraRoll != 0) {
			buffer.writeByte(6);
			buffer.writeShort(spriteCameraRoll);
		}

		if (spriteTranslateX != 0) {
			buffer.writeByte(7);
			buffer.writeShort(spriteTranslateX);
		}

		if (spriteTranslateY != 0) {
			buffer.writeByte(8);
			buffer.writeShort(spriteTranslateY);
		}
		if (!opcode9.equals("null")) {
			buffer.writeByte(9);
			buffer.writeString(opcode9);
		}
		if (stackable != 0) {
			buffer.writeByte(11);
		}

		if (value != 1) {
			buffer.writeByte(12);
			buffer.writeInt(value);
		}

		if (membersObject) {
			buffer.writeByte(16);
		}

		if (primaryMaleModel > -1) {
			buffer.writeByte(23);
			buffer.writeShort(primaryMaleModel);
			buffer.writeByte(maleTranslation);
		}
		
		if (secondaryMaleModel > -1) {
			buffer.writeByte(24);
			buffer.writeShort(secondaryMaleModel);
		}
		
		if (primaryFemaleModel > -1) {
			buffer.writeByte(25);
			buffer.writeShort(primaryFemaleModel);
			buffer.writeByte(femaleTranslation);
		}
		
		if (secondaryFemaleModel > -1) {
			buffer.writeByte(26);
			buffer.writeShort(secondaryFemaleModel);
		}
		
		for (int index = 0; index < 5; index++) {
			if (groundActions[index] != null && !groundActions[index].isEmpty() && !groundActions[index].equals("null")) {
				buffer.writeByte(index + 30);
				buffer.writeString(groundActions[index]);
			}
		}

		for (int index = 0; index < 5; index++) {
			if (itemActions[index] != null && !itemActions[index].isEmpty() && !itemActions[index].equals("null")) {
				buffer.writeByte(index + 35);
				buffer.writeString(itemActions[index]);
			}
		}

		if (originalModelColors != null && modifiedModelColors != null) {
			buffer.writeByte(40);
			int length = Math.min(originalModelColors.length, modifiedModelColors.length);
			buffer.writeByte(length);
			for (int index = 0; index < length; index++) {
				buffer.writeShort(originalModelColors[index]);
				buffer.writeShort(modifiedModelColors[index]);
			}
		}

		if (originalTextureColors != null && modifiedTextureColors != null) {
			buffer.writeByte(41);
			int length = Math.min(originalTextureColors.length, modifiedTextureColors.length);
			buffer.writeByte(length);
			for (int index = 0; index < length; index++) {
				buffer.writeShort(originalTextureColors[index]);
				buffer.writeShort(modifiedTextureColors[index]);
			}
		}

		if (shiftClickIndex != -2) {
			buffer.writeByte(42);
			buffer.writeByte(shiftClickIndex);
		}

		if (searchable) {
			buffer.writeByte(65);
		}

		if (tertiaryMaleEquipmentModel > -1) {
			buffer.writeByte(78);
			buffer.writeShort(tertiaryMaleEquipmentModel);
		}
		
		if (tertiaryFemaleEquipmentModel > -1) {
			buffer.writeByte(79);
			buffer.writeShort(tertiaryFemaleEquipmentModel);
		}
		
		if (primaryMaleHeadPiece > -1) {
			buffer.writeByte(90);
			buffer.writeShort(primaryMaleHeadPiece);
		}
		
		if (primaryFemaleHeadPiece > -1) {
			buffer.writeByte(91);
			buffer.writeShort(primaryFemaleHeadPiece);
		}
		
		if (secondaryMaleHeadPiece > -1) {
			buffer.writeByte(92);
			buffer.writeShort(secondaryMaleHeadPiece);
		}
		
		if (secondaryFemaleHeadPiece > -1) {
			buffer.writeByte(93);
			buffer.writeShort(secondaryFemaleHeadPiece);
		}
		if (category > -1) {
			buffer.writeByte(94);
			buffer.writeShort(category);
		}
		if (spriteCameraYaw != 0) {
			buffer.writeByte(95);
			buffer.writeShort(spriteCameraYaw);
		}

		if (certID > -1) {
			buffer.writeByte(97);
			buffer.writeShort(certID);
		}
		
		if (certTemplateID > -1) {
			buffer.writeByte(98);
			buffer.writeShort(certTemplateID);
		}
		
		if (stackAmounts != null && stackIDs != null) {

			int[] objHolder = new int[10];
			int[] coHolder = new int[10];

			for (int index = 0; index < 10; index++) {
				if (index < stackAmounts.length && stackAmounts[index] != 0) {
					coHolder[index] = stackAmounts[index];
				}
			}

			for (int index = 0; index < 10; index++) {
				if (index < stackIDs.length && stackIDs[index] != 0) {
					objHolder[index] = stackIDs[index];
				}
			}

			for (int index = 0; index < 10; index++) {
				buffer.writeByte(index + 100);
				buffer.writeShort(objHolder[index]);
				buffer.writeShort(coHolder[index]);
			}

		}

		if (groundScaleX > -1) {
			buffer.writeByte(110);
			buffer.writeShort(groundScaleX);
		}

		if (groundScaleY > -1) {
			buffer.writeByte(111);
			buffer.writeShort(groundScaleY);
		}

		if (groundScaleZ > -1) {
			buffer.writeByte(112);
			buffer.writeShort(groundScaleZ);
		}

		if (ambience != 0) {
			buffer.writeByte(113);
			buffer.writeByte(ambience);
		}

		if (diffusion != 0) {
			buffer.writeByte(114);
			buffer.writeByte(diffusion);
		}

		if (team != 0) {
			buffer.writeByte(115);
			buffer.writeByte(team);
		}

		if (unnotedId > -1) {
			buffer.writeByte(139);
			buffer.writeShort(unnotedId);
		}

		if (notedId > -1) {
			buffer.writeByte(140);
			buffer.writeShort(notedId);
		}

		if (placeholderId > -1) {
			buffer.writeByte(148);
			buffer.writeShort(placeholderId);
		}

		if (placeholderTemplateId > -1) {
			buffer.writeByte(149);
			buffer.writeShort(placeholderTemplateId);
		}

		if (Objects.nonNull(params)) {
			buffer.writeByte(249);
			buffer.writeByte(params.size());
			for (int key : params.keySet()) {
				Object value = params.get(key);
				buffer.writeByte(value instanceof String ? 1 : 0);
				buffer.write24BitInt(key);
				if (value instanceof String) {
					buffer.writeString((String) value);
				} else {
					buffer.writeInt((Integer) value);
				}
			}
		}

		buffer.writeByte(0);

		return buffer;
	}

	@Override
	public String toString() {
		return "[" + this.id + "] " + this.name;
	}

	@OrderType(priority = 1)
	public String name = "null";
	@OrderType(priority = 2)
	public String[] groundActions = new String[] { "null", "null", "Take", "null", "null" };
	@OrderType(priority = 3)
	public String[] itemActions = new String[] { "null", "null", "null", "null", "Drop" };
	@OrderType(priority = 4) @MeshIdentifier
	public int modelId;
	@OrderType(priority = 5)
	public int spriteScale = 2000;
	@OrderType(priority = 6)
	public int spriteTranslateX = 0;
	@OrderType(priority = 7)
	public int spriteTranslateY = 0;
	@OrderType(priority = 8)
	public int groundScaleX = 128;
	@OrderType(priority = 9)
	public int groundScaleY = 128;
	@OrderType(priority = 10)
	public int groundScaleZ = 128;
	@OrderType(priority = 11) @MeshIdentifier
	public int primaryMaleModel = -1;
	@OrderType(priority = 12) @MeshIdentifier
	public int secondaryMaleModel = -1;
	@OrderType(priority = 13) @MeshIdentifier
	public int tertiaryMaleEquipmentModel = -1;
	@OrderType(priority = 14)
	public int maleTranslation;
	@OrderType(priority = 15) @MeshIdentifier
	public int primaryMaleHeadPiece = -1;
	@OrderType(priority = 16) @MeshIdentifier
	public int secondaryMaleHeadPiece = -1;
	@OrderType(priority = 17) @MeshIdentifier
	public int primaryFemaleModel = -1;
	@OrderType(priority = 18) @MeshIdentifier
	public int secondaryFemaleModel = -1;
	@OrderType(priority = 19) @MeshIdentifier
	public int tertiaryFemaleEquipmentModel = -1;
	@OrderType(priority = 20)
	public int femaleTranslation;
	@OrderType(priority = 21) @MeshIdentifier
	public int primaryFemaleHeadPiece = -1;
	@OrderType(priority = 22) @MeshIdentifier
	public int secondaryFemaleHeadPiece = -1;
	@OrderType(priority = 23)
	public int[] originalModelColors;
	@OrderType(priority = 24)
	public int[] modifiedModelColors;
	@OrderType(priority = 25)
	public int[] originalTextureColors;
	@OrderType(priority = 26)
	public int[] modifiedTextureColors;
	@OrderType(priority = 27)
	public int spritePitch = 0;
	public int spriteCameraRoll = 0;
	public int spriteCameraYaw = 0;
	public int value = 1;
	public boolean searchable;
	public int stackable = 0;
	public boolean membersObject = false;
	public int ambience;
	public int diffusion;
	public int[] stackAmounts;
	public int[] stackIDs;
	public int certID = -1;
	public int certTemplateID = -1;
	public int team;
	public int shiftClickIndex = -2;
	public int unnotedId = -1;
	public int notedId = -1;
	public int placeholderId = -1;
	public int placeholderTemplateId = -1;
	public HashMap<Integer, Object> params = null;

	private static Map<Field, Integer> fieldPriorities;

	@Override
	public Map<Field, Integer> getPriority() {
		if (fieldPriorities != null)
			return fieldPriorities;
		Map<String, Pair<Field, Object>> values = ReflectionUtils.getValues(this);

		fieldPriorities = Maps.newHashMap();

		values.values().forEach(pair -> {
			Field field = pair.getKey();
			int priority = field.isAnnotationPresent(OrderType.class) ? field.getAnnotation(OrderType.class).priority() : 1000;
			fieldPriorities.put(field, priority);
		});
		return fieldPriorities;
	}

	@Override
	public List<Integer> getMeshIds() {
		List<Integer> meshes = Lists.newArrayList();
		try {
			for (Field field : this.getClass().getFields()) {
				if (field.isAnnotationPresent(MeshIdentifier.class)) {
					if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(short.class)) {
						meshes.add((int) field.get(this));
					} else if (field.getType().isAssignableFrom(int[][].class)) {
						int[][] values = (int[][]) field.get(this);
						for (int type = 0; type < values.length; type++) {
							int[] models = values[type];
							for (int model = 0; model < models.length; model++) {
								meshes.add(models[model]);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return meshes;
	}

	@Override
	public List<Pair<Integer, Integer>> getRecolors() {
		List<Pair<Integer, Integer>> pairs = Lists.newArrayList();
		try {
			if (originalModelColors == null || modifiedModelColors == null) {
				return null;
			}
			int length = Math.min(originalModelColors.length, modifiedModelColors.length);
			for (int index = 0; index < length; index++) {
				pairs.add(new Pair<>(originalModelColors[index], modifiedModelColors[index]));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pairs;
	}

	@Override
	public List<Pair<Integer, Integer>> getRetextures() {
		List<Pair<Integer, Integer>> pairs = Lists.newArrayList();
		try {
			if (originalTextureColors == null || modifiedTextureColors == null) {
				return null;
			}
			int length = Math.min(originalTextureColors.length, modifiedTextureColors.length);
			for (int index = 0; index < length; index++) {
				pairs.add(new Pair<>(originalTextureColors[index], modifiedTextureColors[index]));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pairs;
	}
}
