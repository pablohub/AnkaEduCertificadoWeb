const id = document.getElementById.bind(document)
const q = document.querySelector.bind(document)
const all = document.querySelectorAll.bind(document)

new DataTable('#tblGrupos', {
    language: {
        url: 'https://cdn.datatables.net/plug-ins/2.0.3/i18n/es-ES.json'
    }
})

new DataTable('#tblEstudiante', {
    language: {
        url: 'https://cdn.datatables.net/plug-ins/2.0.3/i18n/es-ES.json'
    }
})

const seccionEliminar = id('seccionEliminar')
const btnEliminarRegistros = id('btnEliminarRegistros')
const btnExportarPdf = id('btnExportarPdf')
const chkAll = id('chkAll')
const chkEstudiantes = all('.chkEstudiantes')
const tbody = q('#tblEstudiante tbody')
const csrfToken = q("input[name='_csrf']").value
const grupoId = q("input[name='grupoId']").value
const pathEstudiante = q("input[name='pathEstudiante']").value
//const chkEstudiantes = [];

seccionEliminar.style.display = 'none'

chkEstudiantes.forEach((checkbox) => {
    checkbox.onchange = (event) => {
        const isChecked = [...chkEstudiantes].some((chk) => chk.checked);
        seccionEliminar.style.display = isChecked ? 'block' : 'none';
    }
});

/*const handleCheckboxChange = () => {
    const isChecked = chkEstudiantes.some((chk) => chk.checked);
    console.log('isChecked:', isChecked);
    seccionEliminar.style.display = isChecked ? 'block' : 'none';
};*/

const formData = new FormData();
formData.append("_csrf", csrfToken);

btnEliminarRegistros.onclick = () => {
    let ids = []
    chkEstudiantes.forEach((chk) => {
        if(chk.checked) ids.push(chk.value)
    })

    fetch('/eliminarEstudiantes', {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-CSRF-Token': csrfToken,
        },
      body: ids.join(','),
    })
      .then(response => response.json())
      .then(data => {
        console.log(data); // Maneja los datos de respuesta aquí
        //loadEstudiantes()
        window.location.href = `${pathEstudiante}/grupo/${grupoId}`;
      })
      .catch(error => console.error('Error:', error));
}

btnExportarPdf.onclick = () => {
    let ids = []
    chkEstudiantes.forEach((chk) => {
        if(chk.checked) ids.push(chk.value)
    })

    downloadPdfs(ids);
}

async function downloadPdfs(ids) {
    try {
        const response = await fetch(`/exportarPdfs/${pathEstudiante}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-Token': csrfToken,
            },
            body: ids.join(','),
        });

        if (!response.ok) {
            throw new Error('Error al descargar el archivo');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'Certificados.zip';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error:', error);
    }
}

chkAll.onclick = (event) => {
    chkEstudiantes.forEach((element) => element.checked = event.target.checked)
}

const loadEstudiantes = () => {
    fetch('/listaEstudiantesAnka', {
      method: 'GET',
      headers: {
          'Content-Type': 'application/json',
          'X-CSRF-Token': csrfToken,
        }
    })
      .then(response => response.json())
      .then(data => {
        console.log(data); // Maneja los datos de respuesta
        let rows = ''
        data.forEach((estudiante, index) => {
            rows += `
                <tr>
                    <td>${++index}</td>
                    <td>${estudiante.codigo}</td>
                    <td>${estudiante.nombres}</td>
                    <td>${estudiante.curso}</td>
                    <td>${estudiante.fechaInicio}</td>
                    <td>${estudiante.fechaFinalizacion}</td>
                    <td>${estudiante.centroCapacitacion}</td>
                    <td>
                        <a href="/certificadoAnka/${estudiante.id}" class="btn btn-primary" target="_blank">Certificado</a>
                    </td>
                    <td>
                        <input class="form-check-input chkEstudiantes" type="checkbox" value="${estudiante.id}">
                    </td>
                </tr>
            `
        })

        tbody.innerHTML = rows
        const checkboxes = all('.chkEstudiantes');
        checkboxes.forEach((checkbox) => {
            checkbox.addEventListener('change', handleCheckboxChange);
            chkEstudiantes.push(checkbox); // Agregamos el checkbox al array
        });
      })
      .catch(error => console.error('Error:', error));

      //dataTableEstudiantes.ajax.reload(null, false).draw(false);
}

//loadEstudiantes()