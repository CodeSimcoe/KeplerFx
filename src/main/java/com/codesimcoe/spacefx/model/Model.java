package com.codesimcoe.spacefx.model;

import java.util.ArrayList;
import java.util.List;

import com.codesimcoe.spacefx.domain.GravityObject;
import com.codesimcoe.spacefx.domain.Particle;

public class Model {

  private static final Model instance = new Model();

  private final List<Particle> particles;
  private final List<GravityObject> gravityObjets;

  private Model() {
    this.particles = new ArrayList<>();
    this.gravityObjets = new ArrayList<>();
  }

  public static Model getInstance() {
    return instance;
  }

  public void addGravityObject(final GravityObject object) {
    this.gravityObjets.add(object);
  }

  public void addParticle(final Particle particle) {
    this.particles.add(particle);
  }

  public List<Particle> getParticles() {
    return this.particles;
  }

  public List<GravityObject> getGravityObjets() {
    return this.gravityObjets;
  }

  public void clearParticles() {
    this.particles.clear();
  }

  public void clearGravityObjects() {
    this.gravityObjets.clear();
  }
}