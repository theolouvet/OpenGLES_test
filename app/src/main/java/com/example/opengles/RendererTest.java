package com.example.opengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RendererTest implements GLSurfaceView.Renderer {



    Mesh cube;
    Mesh_OpenGL cubeGL;

    Mesh cube2;
    Mesh_OpenGL cubeGL2;



    Context context;

    public RendererTest(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);

        cube = new Cube(true);
        cubeGL = new Mesh_OpenGL();
        cubeGL.setContext(context);
        cubeGL.Init(cube.getVertices(), cube.getColor());

        cube2 = new Cube(true);
        cubeGL2 = new Mesh_OpenGL();
        cubeGL2.setContext(context);
        cubeGL2.Init(cube2.getVertices(), cube2.getColor());

    }
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        cubeGL.initProjectionMatrix(width, height);
        cubeGL2.initProjectionMatrix(width, height);
    }
    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0,0,0,0f);
        GLES20.glEnable(GLES20.GL_ATTACHED_SHADERS);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        long time = SystemClock.uptimeMillis() % (1000L * 10);
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);


        Matrix.setIdentityM(cubeGL.getmModelMatrix(),0);
        Matrix.translateM(cubeGL.getmModelMatrix(), 0, -1.f , 2.0f, -8.0f);
        Matrix.rotateM(cubeGL.getmModelMatrix(), 0, angleInDegrees, -1.0f, 0.0f, 1.0f);
        cubeGL.draw();



        Matrix.setIdentityM(cubeGL2.getmModelMatrix(),0);
        Matrix.translateM(cubeGL2.getmModelMatrix(), 0, 1.0f , -4.0f, -6.0f);
        Matrix.rotateM(cubeGL2.getmModelMatrix(), 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
        cubeGL2.draw();

    }
}