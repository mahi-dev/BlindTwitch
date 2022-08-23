package model;

import model.identity.UniquelyIdentifiable;

import java.io.Serializable;

public interface Model<I extends Serializable> extends UniquelyIdentifiable<I>, Serializable {
}
