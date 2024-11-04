const {onCall} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions");
const vision = require("@google-cloud/vision");

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
    const client = new vision.ImageAnnotatorClient();

    const request = {
      image: {
        content: Buffer.from(data.imageBase64, "base64"),
      },
    };

    const [result] = await client.labelDetection(request);
    const labels = result.labelAnnotations;
    const detectedLabels = labels.map((label) => label.description.toLowerCase());

    logger.info("Detected labels:", detectedLabels);

    const descriptionWords = data.description.toLowerCase().split(" ");
    const matchingLabels = detectedLabels.filter((label) =>
      descriptionWords.some((word) =>
        label.includes(word) || word.includes(label),
      ),
    );

    logger.info("Matching labels:", matchingLabels);

    let score = 0;
    if (matchingLabels.length > 0) {
      score = Math.min(Math.ceil(matchingLabels.length / 2), 3);
    }

    return {
      score,
      labels: detectedLabels,
      matchingLabels,
    };
  } catch (error) {
    logger.error("Error in checkPhotoFunction:", error);
    throw new Error("Error processing image: " + error.message);
  }
});
