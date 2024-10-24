const {onCall} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions");
const {VertexAI} = require("@google-cloud/vertexai");


const vertexAI = new VertexAI({project: "engineering-thesis-mobile-game", location: "us-central1"});
const model = "gemini-pro";

exports.checkOpenQuestionFunctionV2 = onCall(async (request) => {
  const data = request.data;
  logger.info("Function called with data:", data);

  const playerAnswer = data.playerAnswer;
  const correctAnswer = data.correctAnswer;
  const question = data.question;

  logger.info("Received playerAnswer:", playerAnswer);
  logger.info("Received correctAnswer:", correctAnswer);
  logger.info("Received question:", question);

  if (!playerAnswer || !correctAnswer || !question) {
    logger.error("One of the parameters is null");
    return {score: 1};
  }

  try {
    const generativeModel = vertexAI.preview.getGenerativeModel({
      model: model,
      generation_config: {
        max_output_tokens: 8, // max tokens 256 -> 200-300 słów liczby <4, 8 dla bezpieczenstwa
        temperature: 0.1, // kreatywnosc -> im większa tym większa losowość
        top_p: 1, // % najbardziej prawdopodobnych tokenów będzie wzięty pod uwagę
        top_k: 40, // wybierz spośród 40 najbardziej prawdopodobnych tokenów
      },
    });

    const prompt = `
    Oto pytanie: ${question}
    Oto poprawna odpowiedź: ${correctAnswer}
    Oto odpowiedź użytkownika: ${playerAnswer}

    Oceń odpowiedź użytkownika w skali 0-20 punktów. Podaj tylko liczbę punktów bez dodatkowych wyjaśnień.
    `;

    const result = await generativeModel.generateContent(prompt);
    const response = await result.response;

    if (response.candidates && response.candidates.length > 0) {
      const generatedText = response.candidates[0].content.parts[0].text;

      const score = parseInt(generatedText.trim(), 10);

      if (isNaN(score) || score < 0 || score > 20) {
        logger.warn("Invalid score generated:", generatedText);
        return {score: 1};
      }

      return {score: score};
    } else {
      logger.warn("No valid response from the model");
      return {score: 1};
    }
  } catch (error) {
    logger.error("Error during AI evaluation:", error);
    return {score: 1};
  }
});
