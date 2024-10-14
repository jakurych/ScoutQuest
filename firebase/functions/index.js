const functions = require("firebase-functions");
const {LanguageServiceClient} = require("@google-cloud/language");

// Inicjalizacja klienta Google Cloud Natural Language API
const client = new LanguageServiceClient();

exports.checkOpenQuestionFunction = functions.https.onCall(async (data, context) => {
  const playerAnswer = data.playerAnswer;
  const correctAnswer = data.correctAnswer;

  try {
    // Analiza odpowiedzi gracza
    const [playerResponse] = await client.analyzeEntities({
      document: {
        content: playerAnswer,
        type: "PLAIN_TEXT",
      },
      encodingType: "UTF8",
    });

    // Analiza poprawnej odpowiedzi
    const [correctResponse] = await client.analyzeEntities({
      document: {
        content: correctAnswer,
        type: "PLAIN_TEXT",
      },
      encodingType: "UTF8",
    });

    // Wyciągnięcie encji z odpowiedzi gracza
    const playerEntities = playerResponse.entities.map((entity) => entity.name.toLowerCase());

    // Wyciągnięcie encji z poprawnej odpowiedzi
    const correctEntities = correctResponse.entities.map((entity) => entity.name.toLowerCase());

    // Obliczenie liczby wspólnych encji
    const commonEntities = playerEntities.filter((entity) => correctEntities.includes(entity));

    // Obliczenie procentowego podobieństwa (np. na podstawie liczby wspólnych encji)
    const similarity = (commonEntities.length / correctEntities.length) * 100;

    // Ustalenie progu zaliczenia (np. 70%)
    const threshold = 70;

    let score;
    if (similarity >= threshold) {
      score = 15; // Punkty za poprawną odpowiedź
    } else {
      score = 0; // Brak punktów
    }

    return {score: score};
  } catch (error) {
    console.error("Error analyzing text:", error);
    throw new functions.https.HttpsError("internal", "Error analyzing text");
  }
});
