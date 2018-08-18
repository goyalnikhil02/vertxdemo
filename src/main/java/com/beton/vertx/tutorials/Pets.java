package com.beton.vertx.tutorials;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import io.vertx.core.json.JsonObject;

public class Pets {
	@Id
	public ObjectId _id;

	public String name;
	public String species;
	public String breed;

	public Pets() {
	}
	
	public Pets(JsonObject json) {
		
		String test=json.getJsonObject("_id").getString("$oid");
		this.name = json.getString("name");
	    this.species = json.getString("species");
	    this.breed=json.getString("breed");
	    this._id=new ObjectId(test);
	    }

	public Pets(ObjectId _id, String name, String species, String breed) {
		this._id = _id;
		this.name = name;
		this.species = species;
		this.breed = breed;
	}

	public String get_id() {
		return _id.toHexString();
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

}
