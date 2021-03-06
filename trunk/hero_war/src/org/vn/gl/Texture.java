package org.vn.gl;


/**
 * Simple container class for textures. Serves as a mapping between Android
 * resource ids and OpenGL texture names, and also as a placeholder object for
 * textures that may or may not have been loaded into vram. Objects can cache
 * Texture objects but should *never* cache the texture name itself, as it may
 * change at any time.
 */
public class Texture extends AllocationGuard {
	/** Id tren TexttureLibrary */
	public int resource;
	/** Id tren gl */
	public int name;
	public int width;
	public int height;
	public boolean loaded;
	public iBitmapInImageCache bitmapInImageCache;
	/**
	 * sau khi add bitmap vào GL = true sẽ xóa bitmap này;false sẽ không
	 */
	public boolean isRecycleBitMap;
	public String name_bitmap;

	public Texture() {
		super();
		reset();
	}

	public void reset() {
		resource = -1;
		name = -1;
		width = 0;
		height = 0;
		loaded = false;
		bitmapInImageCache = null;
		isRecycleBitMap = true;
		name_bitmap = null;
	}

	public void setIsRecyle(boolean isR) {
		isRecycleBitMap = isR;
	}

}
