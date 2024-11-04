const {onCall} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions");
const vision = require("@google-cloud/vision");
const {Translate} = require("@google-cloud/translate").v2;
const natural = require("natural");

exports.checkPhotoFunction = onCall(async (request) => {
  const data = request.data;

  logger.info("Starting photo check function");

  if (!data.imageBase64) {
    logger.error("No image data received");
    throw new Error("No image data received");
  }

  if (!data.description) {
    logger.error("No description received");
    throw new Error("No description received");
  }

  logger.info("Image data length: " + data.imageBase64.length);
  logger.info("Description: " + data.description);

  try {
    // Initialize clients
    const visionClient = new vision.ImageAnnotatorClient();
    const translateClient = new Translate();

    // Translate the description to English
    const [translatedDescription] = await translateClient.translate(
        data.description,
        "en",
    );
    logger.info("Translated description:", translatedDescription);

    // Extract words from the translated description and perform stemming
    const descriptionText = translatedDescription.toLowerCase();
    const tokenizer = new natural.WordTokenizer();
    let descriptionWords = tokenizer.tokenize(descriptionText);

    // Remove stop words
    const stopWords = natural.stopwords;
    descriptionWords = descriptionWords.filter(
        (word) => !stopWords.includes(word),
    );

    // Stem the description words
    const stemmer = natural.PorterStemmer;
    const stemmedDescriptionWords = descriptionWords.map((word) =>
      stemmer.stem(word),
    );

    logger.info("Stemmed description words:", stemmedDescriptionWords);

    if (stemmedDescriptionWords.length === 0) {
      logger.error("No words found in translated description for matching.");
      throw new Error("Invalid description for matching.");
    }

    // Prepare image content
    const imageRequest = {
      image: {
        content: Buffer.from(data.imageBase64, "base64"),
      },
    };

    // Perform label detection
    const [result] = await visionClient.labelDetection(imageRequest);
    const labels = result.labelAnnotations;
    const detectedLabels = labels.map((label) =>
      label.description.toLowerCase(),
    );

    // Stem the detected labels
    const stemmedDetectedLabels = detectedLabels.map((label) =>
      stemmer.stem(label),
    );

    logger.info("Stemmed detected labels:", stemmedDetectedLabels);

    // Match detected labels with description words
    const matchingLabels = stemmedDetectedLabels.filter((label) =>
      stemmedDescriptionWords.includes(label),
    );

    logger.info("Matching labels:", matchingLabels);

    // Calculate score based on matching labels
    let score = 0;
    if (matchingLabels.length > 0) {
      score = Math.min(matchingLabels.length * 5, 15);
    }

    return {
      score,
      labels: detectedLabels,
      matchingLabels,
    };
  } catch (error) {
    logger.error("Error in photo check function:", error);
    throw new Error("Failed to process image");
  }
});
