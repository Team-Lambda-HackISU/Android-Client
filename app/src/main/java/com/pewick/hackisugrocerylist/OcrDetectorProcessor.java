package com.pewick.hackisugrocerylist;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private ArrayList<String> currentDetections;

    OcrDetectorProcessor(){
        currentDetections = new ArrayList<>();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        currentDetections.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
//        Log.d("Processor", "Array Size: " + items.size());
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
//                Log.d("Processor", "Text detected! " + item.getValue());
                for(Text text :item.getComponents()){
                    currentDetections.add(text.getValue());
                }
            }
        }
    }

    public ArrayList<String> getItems(){
        return currentDetections;
    }
}