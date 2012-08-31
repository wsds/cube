package com.cube.opengl.common;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class AnimationManager {
	public ArrayList<AnimationGl> animationGls = new ArrayList<AnimationGl>();

	public AnimationManager() {

	}

	public AnimationGl addAnimationGl(Callback callback) {
		AnimationGl animationGl = new AnimationGl();
		animationGl.callback = callback;
		animationGl.matrix = new GlMatrix();
		animationGls.add(animationGl);

		return animationGl;
	}

	public void removeAnimationGl(AnimationGl animationGl) {
		animationGls.remove(animationGl);
	}

	public void draw(GL10 gl) {
		@SuppressWarnings("unchecked")
		ArrayList<AnimationGl> animationGls = (ArrayList<AnimationGl>) this.animationGls.clone();
		for (AnimationGl animationGl : animationGls) {
			gl.glLoadIdentity();
			gl.glMultMatrixf(animationGl.matrix.data);
			animationGl.callback.ondraw(gl);
			@SuppressWarnings("unchecked")
			ArrayList<GLAnimation2> animationPool = (ArrayList<GLAnimation2>) animationGl.animationPool.clone();
			// Double-buffering here to resolve the ConcurrentModificationException, which to caused by multiple thread accessing.
			for (GLAnimation2 animation : animationPool) {
				boolean isFinished = animation.transformModel(animationGl.matrix);
				if (isFinished == true) {
					animationGl.removeAnimation(animation);
					if (animation.children != null) {
						for (GLAnimation2 child : animation.children) {
							animationGl.addAnimation(child);
						}
					}
				}
			}
			animationPool.clear();
		}
		animationGls.clear();
	}

	public interface Callback {
		public void ondraw(GL10 gl);
	}

	public class AnimationGl {

		public Callback callback = null;
		public GlMatrix matrix = null;
		ArrayList<GLAnimation2> animationPool = new ArrayList<GLAnimation2>();

		public void addAnimation(GLAnimation2 animation) {
			animation.remainRepeatTimes = 1;
			animationPool.add(animation);
		}

		public void removeAnimation(GLAnimation2 animation) {
			animationPool.remove(animation);
		}
	}

}
