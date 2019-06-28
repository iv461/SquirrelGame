package com.ivj.squirrelgame.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Container for saving Entities, implemented using ConcurrentHashMap
 * 
 * @author
 * @see Entity
 *
 */
public class EntitySet {

	private static final int DEFAULT_CAPACITY = 512;
	private Map<UUID, Entity> entities = new HashMap<UUID, Entity>(
			DEFAULT_CAPACITY);

	public EntitySet() {

	}

	/**
	 * Adds a entity, doesn't allow null to be added or the entity's id to be
	 * null
	 * 
	 * @param entity
	 *            to add
	 * @throws NullPointerException
	 *             if null is passed
	 * @throws NullPointerException
	 *             if entity's id is null
	 */
	public void addEntity(Entity entity) {
		if (entity == null) {
			throw new NullPointerException();
		}
		UUID id = entity.getId();
		if (id == null) {
			throw new NullPointerException();
		}
		entities.put(id, entity);

	}

	/*
	 * Removes a Entity specified by the ID
	 *
	 */
	public void removeEntity(UUID id) {
		if (id == null) {
			throw new NullPointerException();
		}
		entities.remove(id);
	}

	/**
	 * Gets an entity by its ID
	 * 
	 * @param id
	 *            ID of Entity
	 * @return if its not found or the parameter is null, it return null
	 * @throws NullPointerException
	 *             if parameter is null
	 */
	public Entity getEntity(UUID id) {
		if (id == null) {
			throw new NullPointerException();
		}
		return entities.get(id);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Entity e : getEntites()) {
			if (e != null) {
				buf.append(e.toString() + "\n");
			}
		}
		return buf.toString();
	}

	/*
	 * Returns iterateable representation of the entities
	 */
	public Collection<Entity> getEntites() {
		return entities.values();
	}

}
