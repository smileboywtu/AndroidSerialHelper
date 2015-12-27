package com.smileboy.utils;

public class LubyCodeConfig {

	private double c;
	private double delta;
	private long counter;
	private String imagefile;

	public LubyCodeConfig(String file) {
		this.c = 0.1;
		this.delta = 0.5;
		this.counter = 0;
		this.imagefile = file;
	}

	public LubyCodeConfig(String file, double c, double delta) {
		this.c = c;
		this.delta = delta;
		this.counter = 0;
		this.imagefile = file;
	}

	public final void resetCounter() {
		this.counter = 0;
	}

	public final void increaseCounter(int value) {
		this.counter += value;
	}

	public final long getCounter() {
		return this.counter;
	}

	public final double getC() {
		return c;
	}

	public final void setC(double c) {
		this.c = c;
	}

	public final double getDelta() {
		return delta;
	}

	public final void setDelta(double delta) {
		this.delta = delta;
	}

	public final String getImagefile() {
		return imagefile;
	}

	public final void setImagefile(String imagefile) {
		this.imagefile = imagefile;
	}

}
