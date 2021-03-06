package org.vn.gl;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

/**
 * A 2D rectangular mesh. Can be drawn textured or untextured. This version is
 * modified from the original Grid.java (found in the SpriteText package in the
 * APIDemos Android sample) to support hardware vertex buffers and to insert
 * edges between grid squares for tiling.
 */
public class Mesh {
	private float red;
	private float green;
	private float blue;
	private float alpha;
	private boolean isSetColorExpress;
	private boolean ONE_MINUS_SRC_ALPHA = true;

	public Mesh(Texture texture, float width, float height) {
		float textureCoordinates[] = { 0.0f, 1.0f, //
				1.0f, 1.0f, //
				0.0f, 0.0f, //
				1.0f, 0.0f, //
		};

		// short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		// setIndices(indices);
		setVerticesMain(BitmapVertices(width, height));
		setTextureCoordinatesMain(textureCoordinates);
		mTexture = texture;
		isSetColorExpress = false;
		red = 1;
		green = 1;
		blue = 1;
		alpha = 1;
	}

	private float[] BitmapVertices(float width, float height) {
		float Vertices[] = { 0.0f, 0.0f, 0.0f,//
				width, 0.0f, 0.0f,//
				0.0f, height, 0.0f, //
				width, height, 0.0f //
		};
		return Vertices;
	}

	public void setWidthHeight(float width, float height) {
		setVerticesMain(BitmapVertices(width, height));
	}

	/**
	 * C-D<br>
	 * |---|<br>
	 * A-B
	 * 
	 * @param A
	 * @param B
	 * @param C
	 * @param D
	 */
	public void setVertices(PointF A, PointF B, PointF C, PointF D) {
		setVerticesMain(BitmapVertices(A, B, C, D));
	}

	private float[] BitmapVertices(PointF A, PointF B, PointF C, PointF D) {
		float Vertices[] = { A.x, A.y, 0.0f,//
				B.x, B.y, 0.0f,//
				C.x, C.y, 0.0f, //
				D.x, D.y, 0.0f //
		};
		return Vertices;
	}

	private Texture mTexture;
	// Our vertex buffer.
	private FloatBuffer mVerticesBuffer = null;
	// The number of indices.
	private int mNumOfIndices = 4;

	// Our index buffer.
	// private ShortBuffer mIndicesBuffer = null;

	// Our UV texture buffer.
	private FloatBuffer mTextureBuffer = null; // New variable.
	private int mNumOfTextureBuffer = 4;

	/**
	 * Render the mesh.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	public void draw(GL10 gl, float mX, float mY, float mAngle, float mXRotate,
			float mYRotate, float mPriority, float scaleX, float scaleY) {
		if (mTexture != null) {
			if (mTexture.resource != -1 && mTexture.loaded == false) {
				BaseObject.sSystemRegistry.longTermTextureLibrary.loadBitmap(
						BaseObject.sSystemRegistry.contextParameters.context,
						gl, mTexture);
			}

			// Enabled the vertices buffer for writing and to be used during
			// rendering.
			gl.glLoadIdentity();
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			// Specifies the location and data format of an array of vertex
			// coordinates to use when rendering.
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.name);

			gl.glScalef(scaleX, scaleY, 0);

			gl.glTranslatef(mX + mXRotate, mY + mYRotate, mPriority);

			gl.glRotatef(mAngle, 0, 0, 1);
			gl.glTranslatef(-mXRotate, -mYRotate, 0);

			if (isSetColorExpress) {
				if (!ONE_MINUS_SRC_ALPHA) {
					gl.glColor4f(red * alpha, green * alpha, blue * alpha,
							alpha);
				} else {
					gl.glColor4f(red, green, blue, alpha);
				}
			}

			// gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices,
			// GL10.GL_UNSIGNED_SHORT, mIndicesBuffer);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mNumOfIndices);

			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			if (isSetColorExpress) {
				gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
	}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	private void setVerticesMain(float[] vertices) {
		int numOfIndices = vertices.length / 3;
		if (mVerticesBuffer == null || mNumOfIndices != numOfIndices) {
			// a float is 4 bytes, therefore we multiply the number if
			// vertices with 4.
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mVerticesBuffer = vbb.asFloatBuffer();
			mNumOfIndices = numOfIndices;
		}
		mVerticesBuffer.clear();
		mVerticesBuffer.put(vertices);
		mVerticesBuffer.position(0);
	}

	public void setVertices(float[][] pVertices) {
		float[] vertices = new float[pVertices.length * 3];
		int indexV = 0;
		for (int i = 0; i < pVertices.length; i++) {
			vertices[indexV++] = pVertices[i][0];
			vertices[indexV++] = pVertices[i][1];
			vertices[indexV++] = 0;
		}
		setVerticesMain(vertices);
	}

	public void setTextureCoordinates(float[][] pTextureCoordinates) {
		float textureCoordinates[] = new float[pTextureCoordinates.length * 2];
		int indexT = 0;
		for (int i = 0; i < pTextureCoordinates.length; i++) {
			textureCoordinates[indexT++] = pTextureCoordinates[i][0];
			textureCoordinates[indexT++] = pTextureCoordinates[i][1];
		}
		setTextureCoordinatesMain(textureCoordinates);
	}

	// /**
	// * Set the indices.
	// *
	// * @param indices
	// */
	// private void setIndices(short[] indices) {
	// short is 2 bytes, therefore we multiply the number if
	// vertices with 2.
	// ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
	// ibb.order(ByteOrder.nativeOrder());
	// mIndicesBuffer = ibb.asShortBuffer();
	// mIndicesBuffer.put(indices);
	// mIndicesBuffer.position(0);
	// mNumOfIndices = indices.length;
	// }

	/**
	 * Set the texture coordinates.
	 * 
	 * @param textureCoords
	 */
	private void setTextureCoordinatesMain(float[] textureCoords) { // New
		// function.
		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		int numOfTextureBuffer = textureCoords.length / 2;
		if (mTextureBuffer == null || mNumOfTextureBuffer != numOfTextureBuffer) {
			ByteBuffer byteBuf = ByteBuffer
					.allocateDirect(textureCoords.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mTextureBuffer = byteBuf.asFloatBuffer();
			mNumOfTextureBuffer = numOfTextureBuffer;
		}
		mTextureBuffer.clear();
		mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
	}

	public void setTextureCoordinates(float scaleX1, float scaleX2,
			float scaleY1, float scaleY2) {
		float textureCoordinates[] = { scaleX1, scaleY2, //
				scaleX2, scaleY2, //
				scaleX1, scaleY1, //
				scaleX2, scaleY1, //
		};
		setTextureCoordinatesMain(textureCoordinates);
	}

	public void setOpacity(float _opacity) {
		alpha = _opacity;
		isSetColorExpress = true;
	}

	public void setColorExpress(float r, float g, float b, float a) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
		isSetColorExpress = true;
	}

	public void setONE_MINUS_SRC_ALPHA(boolean b) {
		ONE_MINUS_SRC_ALPHA = b;
	}

	public void setTexture(Texture pTexture) {
		mTexture = pTexture;
	}
}
