package jaypasha.funpay.utility.render.msdf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.Getter;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import jaypasha.funpay.utility.render.msdf.FontData.AtlasData;
import jaypasha.funpay.utility.render.msdf.FontData.GlyphData;
import jaypasha.funpay.utility.render.msdf.FontData.MetricsData;
import jaypasha.funpay.utility.render.providers.ResourceProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

public final class MsdfFont {

	@Getter
    private final String name;
	private final AbstractTexture texture;
	@Getter
    private final AtlasData atlas;
	@Getter
    private final MetricsData metrics;
	private final Map<Integer, MsdfGlyph> glyphs;
	private final Map<Integer, Map<Integer, Float>> kernings;

	private MsdfFont(String name, AbstractTexture texture, AtlasData atlas, MetricsData metrics, Map<Integer, MsdfGlyph> glyphs, Map<Integer, Map<Integer, Float>> kernings) {
		this.name = name;
		this.texture = texture;
		this.atlas = atlas;
		this.metrics = metrics;
		this.glyphs = glyphs;
		this.kernings = kernings;
	}

	public int getTextureId() {
		return this.texture.getGlId();
	}

	public void applyGlyphs(Matrix4f matrix, VertexConsumer consumer, String text, float size, float thickness, float spacing, float x, float y, float z, int color) {
		int prevChar = -1;
		float startX = x;

		for (int i = 0; i < text.length(); i++) {
			int _char = text.charAt(i);

			if (_char == '\n') {
				x = startX;
				y += this.glyphs.get(prevChar).getHeight(size) + 1.5f;

				continue;
			}

			MsdfGlyph glyph = this.glyphs.get(_char);

			if (glyph == null) continue;

			Map<Integer, Float> kerning = this.kernings.get(prevChar);
			if (kerning != null) {
				x += kerning.getOrDefault(_char, 0.0f) * size;
			}

			x += glyph.apply(matrix, consumer, size, x, y, z, color) + thickness + spacing;
			prevChar = _char;
		}
	}

	public float getWidth(String text, float size) {
		return Arrays.stream(text.split("\n"))
				.map(line -> calculateWidth(line, size, 0.05f, 0f))
				.max(Float::compareTo)
				.orElse(0.0f);
	}

	public float getWidth(String text, float size, float thickness, float spacing) {
		return Arrays.stream(text.split("\n"))
				.map(line -> calculateWidth(line, size, (thickness + 0f * 0.5f) * 0.5f * size, spacing))
				.max(Float::compareTo)
				.orElse(0.0f);
	}

	public float getWidth(String text, float size, float thickness, float outlineThickness, float spacing) {
		return Arrays.stream(text.split("\n"))
				.map(line -> calculateWidth(line, size, (thickness + outlineThickness * 0.5f) * 0.5f * size, spacing))
				.max(Float::compareTo)
				.orElse(0.0f);
	}

	private float calculateWidth(String text, float size, float thickness, float spacing) {
		int prevChar_ = -1;
		float width = 0f;

		for (int i = 0; i < text.length(); i++) {
			int chr_ = text.charAt(i);
			MsdfGlyph glyph = this.glyphs.get(chr_);

			if (Objects.isNull(glyph)) continue;

			Map<Integer, Float> kernings = this.kernings.get(prevChar_);
			if (kernings != null) {
				width += kernings.getOrDefault(chr_, 0.0f) * size;
			}

			width += glyph.getWidth(size) + thickness + spacing;

			prevChar_ = chr_;
		}

		return width;
	}

	public float getHeight(String text, float size) {
		float height = 0;
		int prevChar = -1;

		for (int i = 0; i < text.length(); i++) {
			int _char = text.charAt(i);

			if (_char == '\n') {
				height += this.glyphs.get(prevChar).getHeight(size) + 1.5f;

				continue;
			}

			MsdfGlyph glyph = this.glyphs.get(_char);

			if (glyph == null)
				continue;

			height = Math.max(glyph.getHeight(size), height);
			prevChar = _char;
		}

		return height;
	}

    public static MsdfFont.Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name = "?";
		private Identifier dataIdentifer;
		private Identifier atlasIdentifier;
		
		private Builder() {}
		
		public MsdfFont.Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public MsdfFont.Builder data(String dataFileName) {
			this.dataIdentifer = Identifier.of("pasxalka", "fonts/" + dataFileName + ".json");
			return this;
		}
		
		public MsdfFont.Builder atlas(String atlasFileName) {
			this.atlasIdentifier = Identifier.of("pasxalka", "fonts/" + atlasFileName + ".png");
			return this;
		}
		
		public MsdfFont build() {
			FontData data = ResourceProvider.fromJsonToInstance(this.dataIdentifer, FontData.class);
			AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(this.atlasIdentifier);
			
			if (data == null) {
				throw new RuntimeException("Failed to read font data file: " + this.dataIdentifer.toString() + 
						"; Are you sure this is json file? Try to check the correctness of its syntax.");
			}
			
			RenderSystem.recordRenderCall(() -> texture.setFilter(true, false));
			
			float aWidth = data.atlas().width();
			float aHeight = data.atlas().height();
			Map<Integer, MsdfGlyph> glyphs = data.glyphs().stream()
					.collect(Collectors.<GlyphData, Integer, MsdfGlyph>toMap(
							(glyphData) -> glyphData.unicode(),
							(glyphData) -> new MsdfGlyph(glyphData, aWidth, aHeight)
					));
	
			Map<Integer, Map<Integer, Float>> kernings = new HashMap<>();
			data.kernings().forEach((kerning) -> {
                Map<Integer, Float> map = kernings.computeIfAbsent(kerning.leftChar(), k -> new HashMap<>());

                map.put(kerning.rightChar(), kerning.advance());
			});

			return new MsdfFont(this.name, texture, data.atlas(), data.metrics(), glyphs, kernings);
		}

	}

}