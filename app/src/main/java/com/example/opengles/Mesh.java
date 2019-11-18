package com.example.opengles;

import android.util.Log;

import org.w3c.dom.ls.LSInput;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public abstract class Mesh {

    private float[] vertices;
    private float[] color;
    private float[] texture;

    private List<float[]> listVertices;
    private List<float[]> listColor;

    private FloatBuffer bufferData;
    private FloatBuffer bufferColor;

    public Vector<Float> getVectorData() {
        return vectorData;
    }

    public Vector<Float> getVectorColor() {
        return vectorColor;
    }

    private Vector<Float> vectorData;
    private Vector<Float> vectorColor;

        /*
        public Mesh(){

        }
        public Mesh(float[] verticesData, float[] colorData){
            this.vertices = verticesData;
            this.color = colorData;
        }*/

    public float[] getVertices() {
        return vertices;
    }

    public void Init(float[] vertices, float[] color){
        this.vertices = vertices;
        this.color = color;
    }
    public void Init(float[] vertices, float[] color, float[] texture){
        this.vertices = vertices;
        this.color = color;
        this.texture = texture;
    }
    public void Init(List<float[]> vertices, List<float[]> color){
        this.listVertices = vertices;
        this.listColor = color;

    }

    public void Init(Vector<Float> data, Vector<Float> color){
        this.vectorData = data;
        this.vectorColor = color;
    }


    public void Init(FloatBuffer a, FloatBuffer b){
        this.bufferData = a;
        this.bufferColor = b;
    }

    public FloatBuffer getBufferColor() {
        return bufferColor;
    }

    public FloatBuffer getBufferData(){
        return bufferData;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public void addVerticePosition(float[] pos){
        this.vectorData.add(pos[0]);
        this.vectorData.add(pos[1]);
        this.vectorData.add(pos[2]);
    }
    public void addVerticeColor(float[] col){
        this.vectorColor.add(col[0]);
        this.vectorColor.add(col[1]);
        this.vectorColor.add(col[2]);
        this.vectorColor.add(col[3]);
    }

    public void addVertexPosition(float[] triangle){
        addVerticePosition(Arrays.copyOfRange(triangle,0,3));
        addVerticePosition(Arrays.copyOfRange(triangle,3,6));
        addVerticePosition(Arrays.copyOfRange(triangle,6,9));
    }

    public void addColorTriangle(float[] color){
        addVerticeColor(Arrays.copyOfRange(color,0,4));
        addVerticeColor(Arrays.copyOfRange(color,4,8));
        addVerticeColor(Arrays.copyOfRange(color,8,12));
    }

    public float[] getColor() {
        return color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public List<float[]> getListVertices() {
        return listVertices;
    }

    public void setListVertices(List<float[]> listVertices) {
        this.listVertices = listVertices;
    }

    public List<float[]> getListColor() {
        return listColor;
    }

    public void setListColor(List<float[]> listColor) {
        this.listColor = listColor;
    }

    public float[] getTexture() {
        return texture;
    }
}
