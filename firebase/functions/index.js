/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions");
const {LanguageServiceClient} = require("@google-cloud/language");

const languageClient = new LanguageServiceClient();

exports.checkOpenQuestionFunction = functions.https.onCall(async (data, context) => {
  const playerAnswer = data.playerAnswer;
  const correctAnswer = data.correctAnswer;

  try {
    // Tworzenie dokumentów dla obu odpowiedzi
    const documents = [{
      content: playerAnswer,
      type: "PLAIN_TEXT",
    }, {
      content: correctAnswer,
      type: "PLAIN_TEXT",
    }];

    // Analiza obu tekstów
    const [playerResult] = await languageClient.analyzeEntities({document: documents[0]});
    const [correctResult] = await languageClient.analyzeEntities({document: documents[1]});

    // Wyciąganie encji (entities) z obu tekstów
    const playerEntities = playerResult.entities.map((entity) => entity.name.toLowerCase());
    const correctEntities = correctResult.entities.map((entity) => entity.name.toLowerCase());

    // Porównanie encji
    const commonEntities = playerEntities.filter((entity) => correctEntities.includes(entity));

    // Obliczenie podobieństwa na podstawie wspólnych encji
    const similarity = commonEntities.length / correctEntities.length;

    // Określenie punktacji na podstawie podobieństwa
    let score = 0;
    if (similarity >= 0.8) {
      score = 15; // pełna punktacja
    } else if (similarity >= 0.5) {
      score = 8; // połowa punktów
    } else {
      score = 0; // brak punktów
    }

    return {score: score};
  } catch (error) {
    console.error("Błąd podczas przetwarzania odpowiedzi:", error);
    throw new functions.https.HttpsError("internal", "Błąd podczas przetwarzania odpowiedzi");
  }
});


// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
