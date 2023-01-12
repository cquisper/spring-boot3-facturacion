$("#filePerfil").change(function () {
    perfilPreview(this);
})

function perfilPreview(input) {
    if (input.files && input.files[0]) {
        let reader = new FileReader();
        console.log($('#floatingInputUsername'))
        reader.onload = function (e) {
            $("#contentPreviewPerfil").remove();
            $("#contentRegistro").append(
                `<div class="col-md-4 mb-md-0 p-md-4 align-self-center" id="contentPreviewPerfil">
                 <h5 class="text-white fw-semibold fst-italic">Previsualización de tu perfil!</h5>
                 <img src="${e.target.result}" class="w-100 rounded-circle" alt="preview">
                 <p class="font-monospace text-white-50 fs-4 text-center">${document.querySelector('#floatingInputUsername').value}</p></div>`
            );
        }
        $("#contentForm").removeClass('col-lg-6 col-md-6 col-sm-6');
        reader.readAsDataURL(input.files[0]);
    }
}