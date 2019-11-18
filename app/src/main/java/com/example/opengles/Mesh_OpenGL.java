package com.example.opengles;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Mesh_OpenGL {
    static String fragmentShaderCode;
    static String VertexShaderCode;
    static Bitmap bitmap;

    /**
     * Code glsl executé sur carte graphique
     */

    final static String fragmentShaderCodeTex = ""+
            "precision mediump float;         \n"
            + "varying vec4 v_Color;          \n"
            + "varying vec2 texcoord;          \n" +
            "uniform sampler2D tex0;          \n "
            // triangle per fragment.
            + "void main()                    \n"     //
            + "{                              \n" +
            "gl_FragColor = vec4(1,1,1,1) * texture2D(tex0, texcoord);"
            //      + "
            + "} \n";
    public static final String VertexShaderCodeTex = "" +
            "uniform mat4 u_MVPMatrix;        \n"
            + "attribute vec4 a_Position;     \n"
            + "attribute vec4 a_Color;        \n"
            + "varying vec4 v_Color;          \n"
            +"attribute vec2 texCoord_in; \n" +
            "varying vec2 texcoord; \n"
            + "void main()                    \n"
            + "{                              \n" +
            "texcoord = texCoord_in;"
            + "   v_Color = a_Color;          \n"
            + "   gl_Position = u_MVPMatrix   \n"
            + "               * a_Position;   \n"
            + "}                              \n";

    final static String fragmentShaderCode2 = "" +
            "precision mediump float;         \n"
            // precision in the fragment shader.
            + "varying vec4 v_Color;          \n"
            + "void main()                    \n"
            + "{                              \n"
            + "   gl_FragColor = v_Color;     \n"
            + "} \n";
    public static final String VertexShaderCode2 = "" +
            "uniform mat4 u_MVPMatrix;        \n"
            + "attribute vec4 a_Position;     \n"
            + "attribute vec4 a_Color;        \n"
            + "varying vec4 v_Color;          \n"
            + "void main()                    \n"
            + "{                              \n"
            + "   v_Color = a_Color;          \n"
            + "   gl_Position = u_MVPMatrix   \n"
            + "               * a_Position;   \n"
            + "}                              \n";


    private static final int mBytesPerFloat = 4;
    private final int mStrideBytes = 0;
    private FloatBuffer ModelBuffer;
    private FloatBuffer mModelColors;
    private FloatBuffer mCubeTextureCoordinates;
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private float[] mViewMatrix = new float[16];
    private float[] vertices;
    private float[] textureData;
    private Vector<Float> vectorData;
    private Vector<Float> vectorColor;
    private float[] colorData;
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private int sizeVertice;
    private boolean textureKnow = false;
    private Context context;
    private int program;
    private int mTextureUniformHandle;
    float[] texture;

    private int mTextureCoordinateHandle;


    private final int mTextureCoordinateDataSize = 2;

    private int mTextureDataHandle;


    public Mesh_OpenGL(){

    }

    //Plusieurs methode d'initialisation selon les besoins
    public void Init(List<float[]> triangleVerticeData, List<float[]> cubeColorData){

        fragmentShaderCode = fragmentShaderCode2;
        VertexShaderCode = VertexShaderCode2;
        sizeVertice = triangleVerticeData.size() * 18;
        Log.i("test", " "+ sizeVertice);
        initBufferPositionFromList(triangleVerticeData);
        initBufferColorFromList(cubeColorData);
        int VertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
        int FragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        int program = createProgram(VertexShader, FragmentShader);
        initHandle(program);
        initViewMatrix();

    }

    public void Init(FloatBuffer bufferData, FloatBuffer bufferColor, int nbT){
        sizeVertice = nbT * 3 * 3;
        ModelBuffer = bufferData;
        mModelColors = bufferColor;
        int VertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
        int FragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        program = createProgram(VertexShader, FragmentShader);
        //InitMatrix();
        initHandle(program);
        initViewMatrix();
    }



    public void Init(float[] triangleVerticeData, float[] cubeColorData){
        fragmentShaderCode = fragmentShaderCode2;
        VertexShaderCode = VertexShaderCode2;
        vertices = triangleVerticeData;
        colorData = cubeColorData;
        sizeVertice = vertices.length;

        initBufferPosition();

        initBufferColor();
        int VertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
        int FragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        program = createProgram(VertexShader, FragmentShader);
        //InitMatrix();
        initHandle(program);
        initViewMatrix();
    }
    public void Init(Vector<Float> triangleVerticeData, Vector<Float> cubeColorData){
        fragmentShaderCode = fragmentShaderCode2;
        VertexShaderCode = VertexShaderCode2;
        vectorData = triangleVerticeData;
        vectorColor = cubeColorData;
        sizeVertice = vectorData.size();
        initBufferPositionFromVector();
        initBufferColorFromVector();
        int VertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
        int FragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        program = createProgram(VertexShader, FragmentShader);
        //InitMatrix();
        initHandle(program);
        initViewMatrix();
    }



    public void Init(float[] triangleVerticeData, float[] cubeColorData, float[] texture){
        fragmentShaderCode = fragmentShaderCodeTex;
        VertexShaderCode = VertexShaderCodeTex;
        this.texture = texture;
        vertices = triangleVerticeData;
        colorData = cubeColorData;
        textureData = texture;
        sizeVertice = vertices.length;
        textureKnow = true;


        initBufferPosition();
        initTexture();
        initBufferColor();
        int VertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
        int FragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        program = createProgram(VertexShader, FragmentShader);

        //InitMatrix();
        initHandle(program);
        initViewMatrix();
    }

    private void initTexture(){
        // Load the texture
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        mCubeTextureCoordinates = ByteBuffer.allocateDirect(texture.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(texture).position(0);
        Log.i("test","load texture ");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =false;
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_test, options);

        Log.i("test","load texture " + R.drawable.ic_test + " "+(boolean) (bitmap == null));
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);


    }

    private void initBufferPosition(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat);
        byteBuffer.order(ByteOrder.nativeOrder());
        ModelBuffer = byteBuffer.asFloatBuffer();
        ModelBuffer.put(vertices).position(0);
    }
    private void initBufferPositionFromVector(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vectorData.size() * mBytesPerFloat);
        byteBuffer.order(ByteOrder.nativeOrder());
        ModelBuffer = byteBuffer.asFloatBuffer();
        ModelBuffer.position(0);
        for(Float f: vectorData)
            ModelBuffer.put(f);
    }

    private void initBufferPositionFromList(List<float[]> vertice){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertice.size()* 18 * mBytesPerFloat);
        byteBuffer.order(ByteOrder.nativeOrder());
        ModelBuffer = byteBuffer.asFloatBuffer();
        ModelBuffer.position(0);
        int pos = 0;
        for(float[] floats : vertice){
            //   ModelBuffer.put(floats).position(pos);
            // pos = floats.length - 1;
            for(float f : floats){

                ModelBuffer.put(f);

                Log.i("test pos"," "+pos);
                pos ++;
            }

        }
        ModelBuffer.position(0);
        try{
            Log.i("test", Arrays.toString(ModelBuffer.array()));}
        catch (Exception e){
            Log.i("test", "error", e);
        }


    }

    private void initBufferColorFromList(List<float[]> color){
        mModelColors = ByteBuffer.allocateDirect(color.size()* 24 * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mModelColors.position(0);

        int pos = 0;
        //Log.i("test", "recher"+ color.size()*12*mBytesPerFloat);
        for(float[] floats : color){
            //Log.i("test", "1");
            //mModelColors.put(floats).position(pos);
            //pos = floats.length - 1;
            for(float f : floats){
                // Log.i("test"," "+pos);
                mModelColors.put(f);
                pos++;
            }
        }
        mModelColors.position(0);
        Log.i("test",mModelColors.toString());
        Log.i("test", " " + mModelColors.get(1));
    }

    private void initBufferColor(){
        mModelColors = ByteBuffer.allocateDirect(colorData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mModelColors.put(colorData).position(0);
        Log.i("test",mModelColors.toString());
        Log.i("test", " " + mModelColors.position());
    }
    private void initBufferColorFromVector(){
        mModelColors = ByteBuffer.allocateDirect(vectorColor.size() * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mModelColors.position(0);
        for(Float f : vectorColor)
            mModelColors.put(f);

    }

    private void initViewMatrix() {
        //camera

        // Position
        final float eyeX = 0.0F;
        final float eyeY = 0.0F;
        final float eyeZ = 1.5F;

        // orientation
        final float lookX = 0.0F;
        final float lookY = 0.0F;
        final float lookZ = -10.0F;

        final float upX = 0.0F;
        final float upY = 1.0F;
        final float upZ = 0.0F;


        Matrix.setLookAtM(
                mViewMatrix, 0,
                eyeX, eyeY, eyeZ,
                lookX, lookY, lookZ,
                upX, upY, upZ);
    }

    public void initHandle(int program) {

        mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(program, "a_Color");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(program,"texCoord_in");
        mTextureUniformHandle = GLES20.glGetAttribLocation(program,"tex0");


        GLES20.glUseProgram(program);
    }


    public void initProjectionMatrix(int width, int height) {


        GLES20.glViewport(0, 0, width, height);


        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader);
                throw new RuntimeException("Could not compile program: "
                        + GLES20.glGetShaderInfoLog(shader) + " | " + source);
            }
        }
        return shader;
    }

    //Creation programme shader executé par le GPU
    public int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {

        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {

            GLES20.glAttachShader(programHandle, vertexShaderHandle);


            GLES20.glAttachShader(programHandle, fragmentShaderHandle);


            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES20.glBindAttribLocation(programHandle, 2, "tex_coord");


            GLES20.glLinkProgram(programHandle);


            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);


            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    public void InitMatrix(){
        Matrix.setIdentityM(mModelMatrix, 0);
        setmModelMatrix(mModelMatrix);
    }

    public float[] getmModelMatrix() {
        return mModelMatrix;
    }

    public void setmModelMatrix(float[] matrix){
        this.mModelMatrix = mModelMatrix;
    }

    public void draw(){
        drawVertices();
    }


    public int getProgram() {
        return program;
    }

    public void UseProgram(int program){
        this.program = program;
    }

    public void drawVertices(){
        ModelBuffer.position(0);
        GLES20.glUseProgram(program);
        GLES20.glVertexAttribPointer(
                mPositionHandle,//numero d attribut
                3,//nb composantes
                GLES20.GL_FLOAT,//type
                false,//normalisation
                mStrideBytes,
                ModelBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        mModelColors.position(0);
        GLES20.glVertexAttribPointer(
                mColorHandle,
                4,
                GLES20.GL_FLOAT,
                false,
                mStrideBytes,
                mModelColors);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        if(textureKnow && bitmap != null){
            mCubeTextureCoordinates.position(0);
            GLES20.glVertexAttribPointer(
                    mTextureCoordinateHandle,
                    mTextureCoordinateDataSize,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    mCubeTextureCoordinates
            );


            // GLUtils.texSubImage2D(GLES20.GL_TEXTURE2,0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE,bitmap);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        }


        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(
                mMVPMatrixHandle,
                1, false,
                mMVPMatrix, 0);
        //Log.i("test"," "+sizeVertice/3);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, sizeVertice/3);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);

    }

    public void setContext(Context context) {
        this.context = context;
    }
}