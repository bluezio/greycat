package org.kevoree.modeling.util.maths.structure.impl;

import org.kevoree.modeling.util.maths.structure.KArray2D;

import java.lang.reflect.Array;
import java.util.Arrays;

public class NativeArray2D implements KArray2D {

    private int _nbRows;

    private int _nbColumns;

    private double[] _back;

    public NativeArray2D(int p_nbRows, int p_nbColumns) {
        this._nbRows = p_nbRows;
        this._nbColumns = p_nbColumns;
        this._back = new double[p_nbRows * p_nbColumns];
    }

    @Override
    public int nbRows() {
        return this._nbRows;
    }

    @Override
    public int nbColumns() {
        return this._nbColumns;
    }

    @Override
    public double get(int p_rowIndex, int p_columnIndex) {
        return this._back[(p_rowIndex * this._nbColumns) + p_columnIndex];
    }

    @Override
    public double set(int p_rowIndex, int p_columnIndex, double value) {
        this._back[(p_rowIndex * this._nbColumns) + p_columnIndex] = value;
        return value;
    }

    @Override
    public double add(int rowIndex, int columnIndex, double value) {
        return set(rowIndex, columnIndex, get(rowIndex, columnIndex) + value);
    }

    @Override
    public double mult(int rowIndex, int columnIndex, double value) {
        return set(rowIndex, columnIndex, get(rowIndex, columnIndex) * value);
    }

    @Override
    public void addAll(double value) {
        for(int i=0; i<_nbColumns*_nbRows;i++){
            _back[i]=_back[i]+value;
        }
    }

    @Override
    public void multAll(double value) {
        for(int i=0; i<_nbColumns*_nbRows;i++){
            _back[i]=_back[i]*value;
        }
    }

    @Override
    public void setAll(double value) {
        Arrays.fill(_back,value);
    }

    @Override
    public void addRow(int rowindex, int numRow) {
//todo
    }

    @Override
    public void addCol(int colIndex, int numCol) {
//todo
    }


    @Override
    public KArray2D clone() {
        NativeArray2D newArr=new NativeArray2D(this._nbRows,this._nbColumns);
        System.arraycopy(_back,0,newArr._back,0,_nbColumns*_nbRows);
        return newArr;
    }
}
