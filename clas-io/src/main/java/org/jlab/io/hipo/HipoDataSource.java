/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.io.hipo;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataEventList;
import org.jlab.io.base.DataSource;
import org.jlab.io.evio.EvioDataDescriptor;
import org.jlab.io.evio.EvioDataDictionary;
import org.jlab.io.evio.EvioDataEvent;
import org.jlab.hipo.io.HipoReader;
import org.jlab.hipo.io.HipoRecord;
import org.jlab.io.base.DataSourceType;

/**
 *
 * @author gavalian
 */
public class HipoDataSource implements DataSource {

    HipoReader  reader = null;
    EvioDataDictionary  dictionary = new EvioDataDictionary();
    int                 numberOfRecords = 0;
    int                 currentEventNumber = 0;
    int                 minEventNumber     = 0;
    int                 numberOfEvent      = 0;
    
    public HipoDataSource(){
        this.reader = new HipoReader();
    }
    
    public boolean hasEvent() {
        return (this.currentEventNumber<this.numberOfEvent);
    }

    public void open(File file) {
        this.open(file.getAbsolutePath());
    }

    public void open(String filename) {
        this.reader.open(filename);
        /*
        HipoRecord header = this.reader.getHeaderRecord();
        int  ncount = header.getEventCount();
        System.out.println("[HipoDataSource] ---> dictionary record opened. # entries = " + ncount);
        for(int ev = 0; ev < ncount; ev++){
            byte[] descBytes  = header.getEvent(ev);
            String descString = new String(descBytes);
            //System.out.println("init dictionary : " + descString);
            EvioDataDescriptor  descriptor = new EvioDataDescriptor(descString);
            this.dictionary.addDescriptor(descriptor);
        }*/
        //this.dictionary.show();
        this.minEventNumber = 0;
        this.currentEventNumber = 0;
        this.numberOfEvent      = this.reader.getEventCount();
    }

    public void open(ByteBuffer buff) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void close() {
        
    }

    public int getSize() {
        return this.numberOfEvent - this.minEventNumber;
    }

    public DataEventList getEventList(int start, int stop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public DataEventList getEventList(int nrecords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public DataEvent getNextEvent() {        
        byte[] array             = this.reader.readEvent(this.currentEventNumber);
        this.currentEventNumber++;
        HipoDataEvent  evioEvent = new HipoDataEvent(array,this.reader.getSchemaFactory());
        return evioEvent;
    }

    public DataEvent getPreviousEvent() {
        
        if(this.currentEventNumber>this.minEventNumber+1){
            this.currentEventNumber--; 
            this.currentEventNumber--; 
        }
        byte[] array             = this.reader.readEvent(this.currentEventNumber);
        this.currentEventNumber++;
        HipoDataEvent  evioEvent = new HipoDataEvent(array,this.reader.getSchemaFactory());
        return evioEvent;
    }

    public DataEvent gotoEvent(int index) {
        if(index>=this.minEventNumber&&index<this.numberOfEvent){
            this.currentEventNumber = index;
            byte[] array             = this.reader.readEvent(this.currentEventNumber);
            this.currentEventNumber++;
            HipoDataEvent  evioEvent = new HipoDataEvent(array,this.reader.getSchemaFactory());
            //EvioDataEvent  evioEvent = new EvioDataEvent(array,ByteOrder.LITTLE_ENDIAN,this.dictionary);        
            return evioEvent;
        }
        return null;
    }
    
    public void reset() {
        this.currentEventNumber = 0;
    }

    public int getCurrentIndex() {
        return this.currentEventNumber;
    }
        
    public static void main(String[] args){
        HipoDataSource reader = new HipoDataSource();
        reader.open("test_hipoio.hipo");
        int counter = 0;
        while(reader.hasEvent()==true){
            DataEvent  event = reader.getNextEvent();
            System.out.println("EVENT # " + counter);
            event.show();
            counter++;
        }
    }

    @Override
    public DataSourceType getType() {
        return DataSourceType.FILE;        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void waitForEvents() {
        
    }
}
