document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById('figuritas-grid');
    const sortSelect = document.getElementById('sort-select');
    const checkPegables = document.getElementById('check-pegables');
    const adRewardModal = document.getElementById('adRewardModal');
    let debeOtorgarRecompensa = false;

    function sortFiguritas() {
        if (!grid || !sortSelect || !checkPegables) return;

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

            if (sortBy === 'numero') {
                const valA = parseInt(a.dataset[sortBy], 10) || 0;
                const valB = parseInt(b.dataset[sortBy], 10) || 0;
                return valA - valB;
            }

            const valA = a.dataset[sortBy] || '';
            const valB = b.dataset[sortBy] || '';
            return valA.localeCompare(valB);
        });

        grid.innerHTML = '';
        cards.forEach(card => grid.appendChild(card));
    }

    if (sortSelect && checkPegables) {
        sortSelect.addEventListener('change', sortFiguritas);
        checkPegables.addEventListener('change', sortFiguritas);
        sortFiguritas();
    }

    if (!adRewardModal) {
        return;
    }

    adRewardModal.addEventListener('show.bs.modal', () => {
        debeOtorgarRecompensa = true;
    });

    adRewardModal.addEventListener('hidden.bs.modal', () => {
        if (!debeOtorgarRecompensa) {
            return;
        }

        debeOtorgarRecompensa = false;
        const rewardUrl = adRewardModal.dataset.rewardUrl;

        if (rewardUrl) {
            window.location.href = rewardUrl;
        }
    });
});
