package com.ivj.squirrelgame.board;

import java.util.UUID;

import com.ivj.squirrelgame.core.EntityType;

/**
 * Interface with methods providing a view of the Board so positions can be
 * evaluated without giving access to the underlying objects in this positions.
 * 
 * @author
 *
 */
public interface BoardView {
	public EntityType getEntityType(XY pos);

	/**
	 * Gets last moving direction of entity on position for better rendering,
	 * returns null if this entity is not a dynamic entity and therefore doesn't
	 * have this property.
	 * 
	 * @param pos
	 *            position of entity
	 * @return
	 */
	public XY getLastMovingDirection(XY pos);

	/**
	 * Gets UUID of the entity in this position, used to identify the squirrels
	 * for different squirrel colors in multiplayer game
	 * 
	 * @return
	 */
	public UUID getID(XY pos);

	/**
	 * Returns true if the IDs are of two squirrels and they are related, e.g if
	 * the one is the master of the other or the other way
	 * 
	 * @return
	 */
	public boolean areRelated(UUID id1, UUID id2);

	public XY getSize();
}
