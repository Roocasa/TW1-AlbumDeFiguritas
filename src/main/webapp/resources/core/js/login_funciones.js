const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const MIN_PASSWORD_LENGTH = 6;

export function validarEmail(inputEmailValue) {
  return EMAIL_REGEX.test(inputEmailValue?.trim() ?? "");
}

export function validarPassword(inputPasswordValue) {
  return (inputPasswordValue ?? "").length >= MIN_PASSWORD_LENGTH;
}

export function validarCamposDeLogin(inputEmailValue, inputPasswordValue) {
  return validarEmail(inputEmailValue) && validarPassword(inputPasswordValue);
}

export function validarCamposDeRegistro(
  inputEmailValue,
  inputCountryValue,
  inputPasswordValue,
  inputConfirmPasswordValue
) {
  return (
    (inputCountryValue ?? "").trim().length > 0 &&
    validarCamposDeLogin(inputEmailValue, inputPasswordValue) &&
    inputPasswordValue === inputConfirmPasswordValue
  );
}
