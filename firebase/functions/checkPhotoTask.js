const {onCall} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions");
const vision = require("@google-cloud/vision");
const {Translate} = require("@google-cloud/translate").v2;

exports.checkPhotoFunction = onCall(async (request) => {
  const data = request.data;

  logger.info("Starting photo check function");
  logger.info("Received data:", {
    hasImage: !!data.imageBase64,
    description: data.description,
  });

  if (!data.imageBase64 || !data.description) {
    logger.error("Missing required data");
    throw new Error("Missing required data");
  }

  try {
    const visionClient = new vision.ImageAnnotatorClient();
    const translateClient = new Translate();

    logger.info("Initializing Vision and Translate clients");

    const imageRequest = {
      image: {
        content: Buffer.from(data.imageBase64, "base64"),
      },
      features: [
        {type: "LABEL_DETECTION"},
        {type: "LANDMARK_DETECTION"},
        {type: "OBJECT_LOCALIZATION"},
        {type: "WEB_DETECTION"},
      ],
    };

    logger.info("Sending request to Vision API");
    const [visionResponse] = await visionClient.annotateImage(imageRequest);
    logger.info("Vision API response received");

    logger.info("Translating description to English");
    const [translatedDescription] = await translateClient.translate(
        data.description,
        "en",
    );
    logger.info("Translated description:", translatedDescription);
    const descriptionLower = translatedDescription.toLowerCase();

    const detectedObjects = {
      labels: (visionResponse.labelAnnotations || []).map((label) =>
        label.description.toLowerCase()),
      landmarks: (visionResponse.landmarkAnnotations || []).map((landmark) =>
        landmark.description.toLowerCase()),
      objects: (visionResponse.localizedObjectAnnotations || []).map((obj) =>
        obj.name.toLowerCase()),
      webEntities: (visionResponse.webDetection &&
        visionResponse.webDetection.webEntities || []).map((entity) =>
        entity.description.toLowerCase()),
      similarImages: visionResponse.webDetection &&
        visionResponse.webDetection.visuallySimilarImages || [],
    };

    logger.info("Detected objects:", detectedObjects);

    const calculateSimilarity = (text1, text2) => {
      const set1 = new Set(text1.split(" "));
      const set2 = new Set(text2.split(" "));
      const intersection = new Set([...set1].filter((x) => set2.has(x)));
      return intersection.size / Math.max(set1.size, set2.size);
    };

    let score = 0;
    let confidence = 0;
    const matches = [];

    const isExactLandmarkMatch = detectedObjects.landmarks.some((landmark) =>
      landmark.includes(descriptionLower) ||
      descriptionLower.includes(landmark));

    if (isExactLandmarkMatch) {
      score = 100;
      confidence = 1.0;
      matches.push({
        type: "landmark",
        value: descriptionLower,
        similarity: 1.0,
      });
      logger.info("Exact landmark match found! Maximum score awarded.");
    } else {
      logger.info("No exact landmark match, checking other elements");

      logger.info("Checking labels");
      detectedObjects.labels.forEach((label) => {
        const similarity = calculateSimilarity(label, descriptionLower);
        if (similarity > 0.5) {
          matches.push({type: "label", value: label, similarity});
          score += 5;
          confidence += similarity;
          logger.info(`Label match found: ${label} (score +5)`);
        }
      });

      logger.info("Checking landmarks");
      detectedObjects.landmarks.forEach((landmark) => {
        const similarity = calculateSimilarity(landmark, descriptionLower);
        if (similarity > 0.5) {
          matches.push({type: "landmark", value: landmark, similarity});
          score += 10;
          confidence += similarity * 2;
          logger.info(`Landmark match found: ${landmark} (score +10)`);
        }
      });

      logger.info("Checking objects");
      detectedObjects.objects.forEach((object) => {
        const similarity = calculateSimilarity(object, descriptionLower);
        if (similarity > 0.5) {
          matches.push({type: "object", value: object, similarity});
          score += 7;
          confidence += similarity * 1.5;
          logger.info(`Object match found: ${object} (score +7)`);
        }
      });

      logger.info("Checking web entities");
      detectedObjects.webEntities.forEach((entity) => {
        const similarity = calculateSimilarity(entity, descriptionLower);
        if (similarity > 0.5) {
          matches.push({type: "webEntity", value: entity, similarity});
          score += 3;
          confidence += similarity;
          logger.info(`Web entity match found: ${entity} (score +3)`);
        }
      });
    }


    const finalScore = Math.min(score, 100);
    const finalConfidence = matches.length > 0 ? confidence / matches.length : 0;
    const isCorrect = finalScore >= 15 && finalConfidence >= 0.6;

    logger.info("Final results:", {
      finalScore,
      finalConfidence,
      isCorrect,
      matchesCount: matches.length,
    });

    const finalResult = {
      score: finalScore,
      confidence: finalConfidence,
      isCorrect,
      matches,
      detectedObjects,
    };

    logger.info("Returning result:", finalResult);
    return finalResult;
  } catch (error) {
    logger.error("Error in photo check function:", error);
    throw new Error("Failed to process image");
  }
});
