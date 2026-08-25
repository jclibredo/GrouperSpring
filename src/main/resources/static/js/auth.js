document.getElementById('regForm')?.addEventListener('submit', function(e) {
    const password = document.getElementById('pass').value;
    
    if (password.length < 6) {
        e.preventDefault();
        alert("Password must be at least 6 characters long!");
    }
});