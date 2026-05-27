document.querySelectorAll("[data-password-toggle]").forEach((button) => {
  const inputNode = document.getElementById(button.dataset.passwordToggle);

  if (!inputNode) {
    return;
  }

  button.addEventListener("click", () => {
    const mostrarPassword = inputNode.type === "password";
    const iconoMostrar = button.querySelector(".auth-password-toggle__icon--show");
    const iconoOcultar = button.querySelector(".auth-password-toggle__icon--hide");

    inputNode.type = mostrarPassword ? "text" : "password";
    button.classList.toggle("is-showing", mostrarPassword);

    if (iconoMostrar && iconoOcultar) {
      iconoMostrar.hidden = !mostrarPassword;
      iconoOcultar.hidden = mostrarPassword;
    }

    button.setAttribute("aria-pressed", String(mostrarPassword));
    button.setAttribute(
      "aria-label",
      mostrarPassword ? "Ocultar contrasena" : "Mostrar contrasena"
    );
    inputNode.focus();
  });
});
