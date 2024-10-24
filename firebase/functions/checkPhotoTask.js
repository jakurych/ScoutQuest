const {onCall} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions");
const vision = require("@google-cloud/vision");


exports.checkPhotoFunction = onCall(async (request) => {
  const data = request.data;
  const imageBase64 = data.imageBase64; // The image in base64 format
  const description = data.description; // The description of what should be in the photo

  logger.info("Function called with data:", data);

  if (!imageBase64 || !description) {
    logger.error("Missing 'imageBase64' or 'description' arguments.");
    throw new Error("The function had been called without arguments.");
  }

  try {
    const client = new vision.ImageAnnotatorClient();

    const request = {
      image: {
        content: Buffer.from(imageBase64, "base64"), // Użyj podwójnych cudzysłowów
      },
    };

    const [result] = await client.labelDetection(request);
    const labels = result.labelAnnotations;
    const detectedLabels = labels.map((label) => label.description.toLowerCase());

    const descriptionLower = description.toLowerCase();

    // Usunięto nieużywaną zmienną isMatch

    // Return a score based on matching labels
    const matchingLabels = detectedLabels.filter((label) => descriptionLower.includes(label) || label.includes(descriptionLower));
    const score = matchingLabels.length > 0 ? 1 : 0;

    return {score, labels: detectedLabels};
  } catch (error) {
    logger.error("Error processing image", error);
    throw new Error("Error processing image.");
  }
});
