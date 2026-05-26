document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById('figuritas-grid');
    const sortSelect = document.getElementById('sort-select');
    const checkPegables = document.getElementById('check-pegables');

    function sortFiguritas() {
        if (!grid) return; // Por si entramos a otra página

        const cards = Array.from(grid.querySelectorAll('.figurita-card'));
        const sortBy = sortSelect.value;
        const pegablesFirst = checkPegables.checked;

        cards.sort((a, b) => {
            if (pegablesFirst) {
                const aPegable = a.dataset.pegable === 'true' ? 1 : 0;
                const bPegable = b.dataset.pegable === 'true' ? 1 : 0;

                if (aPegable !== bPegable) {
                    return bPegable - aPegable;
                }
            }

            if (sortBy === 'numero' || sortBy === 'score') {
                const valA = parseInt(a.dataset[sortBy]) || 0;
                const valB = parseInt(b.dataset[sortBy]) || 0;
                return sortBy === 'score' ? (valB - valA) : (valA - valB);
            } else {
                const valA = a.dataset[sortBy] || '';
                const valB = b.dataset[sortBy] || '';
                return valA.localeCompare(valB);
            }
        });

        grid.innerHTML = '';
        cards.forEach(card => grid.appendChild(card));
    }

    if (sortSelect && checkPegables) {
        sortSelect.addEventListener('change', sortFiguritas);
        checkPegables.addEventListener('change', sortFiguritas);
    }

    sortFiguritas();
});
