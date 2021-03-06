package org.vn.gl;

/**
 * The core object from which most other objects are derived. Anything that will
 * be managed by an ObjectManager, and anything that requires an update per
 * frame should be derived from BaseObject. BaseObject also defines the
 * interface for the object-wide system registry.
 */
public abstract class BaseObject extends AllocationGuard {
	public static ObjectRegistry sSystemRegistry = null;

	public BaseObject() {
		super();
	}

	/**
	 * Update this object.
	 * 
	 * @param timeDelta
	 *            The duration since the last update (in seconds).
	 * @param parent
	 *            The parent of this object (may be NULL).
	 */
	public void update(float timeDelta, BaseObject parent) {
		// Base class does nothing.
	}

	public void reset() {

	}

}