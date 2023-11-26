
//        try {
//            Model model = Model.newInstance(getApplicationContext());
//
//            // Creates inputs for reference.
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
//            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
//            byteBuffer.order(ByteOrder.nativeOrder());
//
//            int[] intValues = new int[imageSize * imageSize];
//            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
//            int pixel = 0;
//            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
//            for(int i = 0; i < imageSize; i ++){
//                for(int j = 0; j < imageSize; j++){
//                    int val = intValues[pixel++]; // RGB
//                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
//                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
//                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
//                }
//            }
//
//            inputFeature0.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            Model.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            float[] confidences = outputFeature0.getFloatArray();
//            // find the index of the class with the biggest confidence.
//            int maxPos = 0;
//            float maxConfidence = 0;
//            for (int i = 0; i < confidences.length; i++) {
//                if (confidences[i] > maxConfidence) {
//                    maxConfidence = confidences[i];
//                    maxPos = i;
//                }
//            }
//            String[] classes = {"Apple", "Banana", "Orange"};
//            result.setText(classes[maxPos]);
//
//            // Releases model resources if no longer used.
//            model.close();
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }
//    }