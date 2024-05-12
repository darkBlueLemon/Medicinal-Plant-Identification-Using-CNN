import os
import requests
from flask import Flask, request, jsonify
import numpy as np
from PIL import Image
import io
import cv2
import tensorflow as tf
import tensorflow_hub as hub

my_custom_objects = {'KerasLayer': hub.KerasLayer}
model = tf.keras.models.load_model("ensemble_vgg16_efficientnetv2_9734.h5", custom_objects=my_custom_objects)

app = Flask(__name__)

@app.route('/classify', methods=['POST'])
def classify():
    if 'image' not in request.files:
        return jsonify({'error': 'No file provided'}), 400

    # Get the file from the request
    file = request.files['image']

    # Read the file as an image
    image = Image.open(file)

    # Preprocess the image for model input (resize, normalize, convert to array)
    processed_image = preprocess_image(image)

    # Get the class label with highest probability
    # predicted_class = np.argmax(predictions[0])

    # result = {'class': predicted_class, 'confidence': float(predictions[0][predicted_class])}
    # return jsonify(result)

    info = request.form.get('info')

    result = {'class': processed_image, 'confi': 23}
    return jsonify(result)

def preprocess_image(image):
    # img_path = r"/Users/darkbluelemon/Downloads/one.jpg"
    # img = cv2.imread(img_path)
    img = image
    resized_img = tf.image.resize(img, (224,224))
    image = resized_img
    image_batch = np.expand_dims(image, axis=0)
    image_batch_repeated = np.tile(image_batch, (32, 1, 1, 1))
    print(image_batch_repeated.shape)  # Output: (32, 224, 224, 3)

    # plt.imshow(resized_img.numpy().astype(int))
    # plt.show()

    batch_prediction = model.predict(image_batch_repeated)
    # print(batch_prediction)
    # print("predicted label:",class_names[np.argmax(batch_prediction[0])])
    # print("confi:",class_names[np.argmax(batch_prediction[0])])

    print(np.argmax(batch_prediction))
    class_names = ['Aloevera', 'Amla', 'Amruthaballi', 'Arali', 'Astma_weed', 'Badipala', 'Balloon_Vine', 'Bamboo', 'Beans', 'Betel', 'Bhrami', 'Bringaraja', 'Caricature', 'Castor', 'Catharanthus', 'Chakte', 'Chilly', 'Citron lime (herelikai)', 'Coffee', 'Common rue(naagdalli)', 'Coriender', 'Curry', 'Doddpathre', 'Drumstick', 'Ekka', 'Eucalyptus', 'Ganigale', 'Ganike', 'Gasagase', 'Ginger', 'Globe Amarnath', 'Guava', 'Henna', 'Hibiscus', 'Honge', 'Insulin', 'Jackfruit', 'Jasmine', 'Kambajala', 'Kasambruga', 'Kohlrabi', 'Lantana', 'Lemon', 'Lemongrass', 'Malabar_Nut', 'Malabar_Spinach', 'Mango', 'Marigold', 'Mint', 'Neem', 'Nelavembu', 'Nerale', 'Nooni', 'Onion', 'Padri', 'Palak(Spinach)', 'Papaya', 'Parijatha', 'Pea', 'Pepper', 'Pomoegranate', 'Pumpkin', 'Raddish', 'Rose', 'Sampige', 'Sapota', 'Seethaashoka', 'Seethapala', 'Spinach1', 'Tamarind', 'Taro', 'Tecoma', 'Thumbe', 'Tomato', 'Tulsi', 'Turmeric', 'ashoka', 'camphor', 'kamakasturi', 'kepala']
    predicted_label = class_names[np.argmax(batch_prediction)]
    print(predicted_label)

    return predicted_label

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=4529, debug=True)