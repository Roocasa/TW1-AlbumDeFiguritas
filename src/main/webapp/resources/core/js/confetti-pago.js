(function () {
  if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
    return;
  }

  const canvas = document.createElement("canvas");
  const context = canvas.getContext("2d");
  const colors = ["#f9c12f", "#119b5f", "#185bb8", "#da291c", "#ffffff"];
  const pieces = [];
  const duration = 1600;
  const start = performance.now();

  canvas.setAttribute("aria-hidden", "true");
  canvas.style.position = "fixed";
  canvas.style.inset = "0";
  canvas.style.zIndex = "2000";
  canvas.style.pointerEvents = "none";
  document.body.appendChild(canvas);

  function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  }

  function createPiece(index) {
    return {
      x: canvas.width * (0.18 + Math.random() * 0.64),
      y: -20 - Math.random() * 80,
      size: 7 + Math.random() * 8,
      color: colors[index % colors.length],
      speed: 2.2 + Math.random() * 3.6,
      drift: -2.2 + Math.random() * 4.4,
      rotation: Math.random() * Math.PI,
      spin: -0.18 + Math.random() * 0.36
    };
  }

  function fillPieces() {
    pieces.length = 0;
    const total = Math.min(160, Math.max(90, Math.floor(canvas.width / 9)));
    for (let index = 0; index < total; index += 1) {
      pieces.push(createPiece(index));
    }
  }

  function drawPiece(piece) {
    context.save();
    context.translate(piece.x, piece.y);
    context.rotate(piece.rotation);
    context.fillStyle = piece.color;
    context.fillRect(-piece.size / 2, -piece.size / 3, piece.size, piece.size * 0.55);
    context.restore();
  }

  function animate(now) {
    const elapsed = now - start;
    context.clearRect(0, 0, canvas.width, canvas.height);

    pieces.forEach((piece) => {
      piece.x += piece.drift;
      piece.y += piece.speed;
      piece.rotation += piece.spin;
      drawPiece(piece);
    });

    if (elapsed < duration) {
      requestAnimationFrame(animate);
      return;
    }

    canvas.remove();
    window.removeEventListener("resize", handleResize);
  }

  function handleResize() {
    resizeCanvas();
    fillPieces();
  }

  resizeCanvas();
  fillPieces();
  window.addEventListener("resize", handleResize);
  requestAnimationFrame(animate);
})();
