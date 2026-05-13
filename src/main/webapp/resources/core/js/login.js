import { validarCamposDeLogin } from "./login_funciones.js";

const btnLoginNode = document.getElementById("btn-login");
const inputEmailNode = document.getElementById("email");
const inputPasswordNode = document.getElementById("password");

function actualizarEstadoDelBoton() {
  btnLoginNode.disabled = !validarCamposDeLogin(inputEmailNode.value, inputPasswordNode.value);
}

inputEmailNode.addEventListener("input", actualizarEstadoDelBoton);
inputPasswordNode.addEventListener("input", actualizarEstadoDelBoton);

actualizarEstadoDelBoton();
