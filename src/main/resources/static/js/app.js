
(function () {
    'use strict';

    var body = document.body;

    /* ------------------------------------------------ Drawer (navigation mobile) */
    function setDrawer(open) {
        body.dataset.drawer = open ? 'open' : 'closed';
        var burger = document.querySelector('[data-drawer-toggle]');
        if (burger) {
            burger.setAttribute('aria-expanded', String(open));
        }
    }

    document.querySelectorAll('[data-drawer-toggle]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            setDrawer(body.dataset.drawer !== 'open');
        });
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && body.dataset.drawer === 'open') {
            setDrawer(false);
            var burger = document.querySelector('[data-drawer-toggle]');
            if (burger) { burger.focus(); }
        }
    });

    var scrim = document.querySelector('.scrim');
    if (scrim) {
        scrim.addEventListener('click', function () { setDrawer(false); });
    }

    /* ------------------------------------------------ Notifications éphémères */
    document.querySelectorAll('.alert__close').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var alert = btn.closest('.alert');
            if (alert) { alert.remove(); }
        });
    });

    // Masquage automatique des notifications de succès (échec conservé).
    document.querySelectorAll('.alert--success').forEach(function (alert) {
        window.setTimeout(function () { alert.remove(); }, 6000);
    });

    /* ------------------------------------------------ Lignes de commande dynamiques */
    function nextIndex(tbody) {
        var max = -1;
        tbody.querySelectorAll('input[name^="lignes"], select[name^="lignes"]').forEach(function (el) {
            var m = /lignes\[(\d+)\]/.exec(el.name);
            if (m) { max = Math.max(max, parseInt(m[1], 10)); }
        });
        return max + 1;
    }

    window.addLigne = function () {
        var table = document.getElementById('lignes-table');
        var model = document.getElementById('ligne-model');
        if (!table || !model) { return; }

        var tbody = table.querySelector('tbody');
        var index = nextIndex(tbody);
        var holder = document.createElement('tbody');
        holder.innerHTML = model.innerHTML.replace(/__\$\{i\}/g, index).trim();
        var row = holder.firstElementChild;
        tbody.appendChild(row);

        var firstField = row.querySelector('select, input');
        if (firstField) { firstField.focus(); }
    };

    window.removeLigne = function (button) {
        var tbody = button.closest('tbody');
        if (tbody.querySelectorAll('tr').length <= 1) {
            window.alert('La commande doit contenir au moins une ligne.');
            return;
        }
        button.closest('tr').remove();
    };

    /* ------------------------------------------------ Confirmation de suppression */
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (!window.confirm(form.dataset.confirm)) {
                e.preventDefault();
            }
        });
    });

    /* ------------------------------------------------ Remplissage des comptes de démo */
    document.querySelectorAll('[data-fill-user]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var parts = btn.dataset.fillUser.split('|');
            var username = document.getElementById('username');
            var password = document.getElementById('password');
            if (username) { username.value = parts[0]; }
            if (password && parts[1]) { password.value = parts[1]; }
            if (username) { username.focus(); }
        });
    });

    /* ------------------------------------------------ Animations d'entrée des KPI */
    document.querySelectorAll('.kpi').forEach(function (el, i) {
        el.style.animation = 'kpiIn .32s cubic-bezier(.4,0,.2,1) backwards';
        el.style.animationDelay = (i * 45) + 'ms';
    });
})();