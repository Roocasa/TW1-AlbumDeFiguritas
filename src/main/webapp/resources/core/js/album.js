const countryFlags = {
  MEX: "mx",
  CZE: "cz",
  RSA: "za",
  KOR: "kr",
  CAN: "ca",
  BIH: "ba",
  QAT: "qa",
  SUI: "ch",
  BRA: "br",
  HAI: "ht",
  MAR: "ma",
  SCO: "gb-sct",
  USA: "us",
  AUS: "au",
  PAR: "py",
  TUR: "tr",
  CUW: "cw",
  ECU: "ec",
  GER: "de",
  CIV: "ci",
  NED: "nl",
  JPN: "jp",
  SWE: "se",
  TUN: "tn",
  BEL: "be",
  EGY: "eg",
  IRN: "ir",
  NZL: "nz",
  CPV: "cv",
  KSA: "sa",
  ESP: "es",
  URU: "uy",
  FRA: "fr",
  NOR: "no",
  SEN: "sn",
  IRQ: "iq",
  ALG: "dz",
  ARG: "ar",
  AUT: "at",
  JOR: "jo",
  COL: "co",
  JAM: "jm",
  POR: "pt",
  UZB: "uz",
  CRO: "hr",
  ENG: "gb-eng",
  GHA: "gh",
  PAN: "pa",
};

const normalizeText = (text) =>
  text
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");

const buildFlagImage = (countryCode, flagCodeFromData) => {
  const flagCode = flagCodeFromData || countryFlags[countryCode];

  if (!flagCode) {
    return null;
  }

  const image = document.createElement("img");
  image.src = `https://flagcdn.com/${flagCode}.svg`;
  image.alt = countryCode;
  return image;
};

const replaceBadgeWithFlag = (badge, countryCode, flagCode) => {
  const flagImage = buildFlagImage(countryCode, flagCode);

  badge.title = countryCode;
  badge.setAttribute("aria-label", countryCode);

  if (flagImage) {
    badge.replaceChildren(flagImage);
  }
};

const initializeCountryCards = () => {
  const countryUrl = document.body.dataset.countryUrl;

  document.querySelectorAll(".country-page").forEach((card) => {
    const codeBadge = card.querySelector("header span");
    const code = codeBadge.textContent.trim();

    card.dataset.countryCode = code;
    replaceBadgeWithFlag(codeBadge, code, codeBadge.dataset.flagCode);

    if (countryUrl) {
      const url = `${countryUrl}${code}`;

      card.tabIndex = 0;
      card.setAttribute("role", "link");

      card.addEventListener("click", () => {
        window.location.href = url;
      });

      card.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          window.location.href = url;
        }
      });
    }
  });
};

const initializeAlbumFilters = () => {
  const groupFilter = document.getElementById("album-group-filter");
  const countryFilter = document.getElementById("album-country-filter");
  const clearFilters = document.getElementById("album-clear-filters");
  const sections = Array.from(document.querySelectorAll(".album-section"));

  if (!groupFilter || !countryFilter || !clearFilters || sections.length === 0) {
    return;
  }

  const applyAlbumFilters = () => {
    const selectedGroup = groupFilter.value;
    const countrySearch = normalizeText(countryFilter.value.trim());

    sections.forEach((section) => {
      const groupMatches = !selectedGroup || section.id === selectedGroup;
      let hasVisibleCountry = false;

      section.querySelectorAll(".country-page").forEach((card) => {
        const countryName = normalizeText(card.querySelector("h3").textContent);
        const countryCode = normalizeText(card.dataset.countryCode);
        const countryMatches =
          !countrySearch ||
          countryName.includes(countrySearch) ||
          countryCode.includes(countrySearch);
        const isVisible = groupMatches && countryMatches;

        card.hidden = !isVisible;
        hasVisibleCountry = hasVisibleCountry || isVisible;
      });

      section.hidden = !groupMatches || !hasVisibleCountry;
    });
  };

  groupFilter.addEventListener("change", applyAlbumFilters);
  countryFilter.addEventListener("input", applyAlbumFilters);
  clearFilters.addEventListener("click", () => {
    groupFilter.value = "";
    countryFilter.value = "";
    applyAlbumFilters();
    countryFilter.focus();
  });
};

const initializeCountryDetailFlag = () => {
  const countryBadge = document.querySelector(".country-detail-hero__badge");

  if (!countryBadge) {
    return;
  }

  replaceBadgeWithFlag(countryBadge, countryBadge.dataset.countryCode);
};

initializeCountryCards();
initializeAlbumFilters();
initializeCountryDetailFlag();
