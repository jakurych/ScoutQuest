const {onCall} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions");
const vision = require("@google-cloud/vision");
const {Translate} = require("@google-cloud/translate").v2;
const natural = require("natural");
const {lemmatizer} = require("lemmatizer");

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

  logger.info(`Image data length: ${data.imageBase64.length}`);
  logger.info(`Description: ${data.description}`);

  try {
    const visionClient = new vision.ImageAnnotatorClient();
    const translateClient = new Translate();

    //Translate to English
    const [translatedDescription] = await translateClient.translate(
        data.description,
        "en",
    );
    logger.info("Translated description:", translatedDescription);

    // Extract words from the translated description and perform lemmatization
    const descriptionText = translatedDescription.toLowerCase();
    const tokenizer = new natural.WordTokenizer();
    let descriptionWords = tokenizer.tokenize(descriptionText);

    //Remove stop words
    const stopWords = natural.stopwords;
    descriptionWords = descriptionWords.filter(
        (word) => !stopWords.includes(word),
    );

    //Lemmatize the description words
    const lemmatizedDescriptionWords = descriptionWords.map((word) =>
      lemmatizer(word),
    );

    logger.info("Lemmatized description words:", lemmatizedDescriptionWords);

    if (lemmatizedDescriptionWords.length === 0) {
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

    //Lemmatize the detected labels
    const lemmatizedDetectedLabels = detectedLabels.map((label) => {
      const words = tokenizer.tokenize(label);
      const lemmatizedWords = words.map((word) => lemmatizer(word));
      return lemmatizedWords.join(" ");
    });

    logger.info("Lemmatized detected labels:", lemmatizedDetectedLabels);

    //Match lemmatized labels with lemmatized description words
    const matchingLabels = lemmatizedDetectedLabels.filter((label) =>
      lemmatizedDescriptionWords.includes(label),
    );

    logger.info("Matching labels:", matchingLabels);

    let score = 0;
    if (matchingLabels.length > 0) {
      score = Math.min(matchingLabels.length * 5, 15);
    }

    logger.info(`Score: ${score}`);

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
