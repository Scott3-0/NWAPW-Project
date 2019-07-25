package com.example.plantidentifier;

public class ProcessImage {

   @Override
   protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       //load tensorflow file
       try {
           tflite = new Interpreter(loadModelFiles());
       }
       catch (Exception ex){
           ex.printStackTrace();
       }

       
   }
}
