import { validarCamposDeRegistro } from "./login_funciones.js";

const btnRegisterNode = document.getElementById("btn-registrarme");
const inputEmailNode = document.getElementById("email");
const inputCountryNode = document.getElementById("pais");
const inputPasswordNode = document.getElementById("password");
const inputConfirmPasswordNode = document.getElementById("confirm-password");
const passwordHintNode = document.getElementById("password-hint");

function actualizarEstadoDelBoton() {
  const passwordValue = inputPasswordNode.value;
  const confirmPasswordValue = inputConfirmPasswordNode.value;
  const passwordMatch =
    confirmPasswordValue.length === 0 || passwordValue === confirmPasswordValue;

  btnRegisterNode.disabled = !validarCamposDeRegistro(
    inputEmailNode.value,
    inputCountryNode.value,
    passwordValue,
    confirmPasswordValue
  );

  if (passwordHintNode) {
    passwordHintNode.textContent = passwordMatch
      ? "Usa al menos 6 caracteres."
      : "Las contrasenas no coinciden.";
    passwordHintNode.classList.toggle("is-error", !passwordMatch);
  }
}

inputEmailNode.addEventListener("input", actualizarEstadoDelBoton);
inputCountryNode.addEventListener("change", actualizarEstadoDelBoton);
inputPasswordNode.addEventListener("input", actualizarEstadoDelBoton);
inputConfirmPasswordNode.addEventListener("input", actualizarEstadoDelBoton);

actualizarEstadoDelBoton();
