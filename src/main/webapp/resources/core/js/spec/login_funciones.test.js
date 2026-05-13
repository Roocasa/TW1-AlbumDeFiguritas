/**
DOCU DE JASMINE: https://jasmine.github.io/api/5.8/global
**/
import {
  validarCamposDeLogin,
  validarCamposDeRegistro,
  validarEmail,
  validarPassword,
} from "../login_funciones.js";

describe("Login Funciones", function() {
  it("debe validar un email con formato correcto", function() {
    expect(validarEmail("test@unlam.edu.ar")).toBe(true);
  });

  it("debe rechazar un email con formato incorrecto", function() {
    expect(validarEmail("test@unlam")).toBe(false);
  });

  it("debe aceptar una contrasena de al menos 6 caracteres", function() {
    expect(validarPassword("123456")).toBe(true);
  });

  it("debe rechazar una contrasena corta", function() {
    expect(validarPassword("123")).toBe(false);
  });

  it("debe devolver true cuando el email y la contrasena de login son validos", function() {
    expect(validarCamposDeLogin("test@unlam.edu.ar", "123456")).toBe(true);
  });

  it("debe devolver false cuando el email de login es invalido", function() {
    expect(validarCamposDeLogin("test@unlam", "123456")).toBe(false);
  });

  it("debe devolver false cuando la contrasena de login es corta", function() {
    expect(validarCamposDeLogin("test@unlam.edu.ar", "123")).toBe(false);
  });

  it("debe devolver true cuando los datos de registro son validos", function() {
    expect(validarCamposDeRegistro("test@unlam.edu.ar", "Argentina", "123456", "123456")).toBe(true);
  });

  it("debe devolver false cuando las contrasenas del registro no coinciden", function() {
    expect(validarCamposDeRegistro("test@unlam.edu.ar", "Argentina", "123456", "654321")).toBe(false);
  });

  it("debe devolver false cuando el pais del registro no fue seleccionado", function() {
    expect(validarCamposDeRegistro("test@unlam.edu.ar", "", "123456", "123456")).toBe(false);
  });
});
