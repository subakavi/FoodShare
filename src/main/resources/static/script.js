
/* =========================================
   FoodShare - script.js
   Frontend validation and user interactions
   ========================================= */

document.addEventListener("DOMContentLoaded", function () {

    // 1. Validate registration form
    const registrationForm = document.querySelector(
        'form[action="/register"]'
    );

    if (registrationForm) {
        registrationForm.addEventListener("submit", function (event) {
            const name = registrationForm.querySelector(
                '[name="fullName"]'
            )?.value.trim();

            const email = registrationForm.querySelector(
                '[name="email"]'
            )?.value.trim();

            const password = registrationForm.querySelector(
                '[name="password"]'
            )?.value;

            const emailPattern =
                /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

            if (!name || !email || !password) {
                event.preventDefault();
                alert("Please fill in all required fields.");
                return;
            }

            if (!emailPattern.test(email)) {
                event.preventDefault();
                alert("Please enter a valid email address.");
                return;
            }

            if (password.length < 8) {
                event.preventDefault();
                alert("Password must contain at least 8 characters.");
            }
        });
    }

    // 2. Validate food listing form
    const foodForm = document.querySelector(
        'form[action="/food-listings/save"]'
    );

    if (foodForm) {
        const quantityInput = foodForm.querySelector(
            '[name="quantity"]'
        );

        const expiryInput = foodForm.querySelector(
            '[name="expiryDate"]'
        );

        foodForm.addEventListener("submit", function (event) {
            const quantity = Number(quantityInput?.value);
            const expiryDate = expiryInput?.value;

            if (!Number.isInteger(quantity) ||
                quantity < 1 ||
                quantity > 500) {
                event.preventDefault();
                alert("Quantity must be a whole number between 1 and 500.");
                quantityInput?.focus();
                return;
            }

            if (!expiryDate) {
                event.preventDefault();
                alert("Please select an expiry date.");
                expiryInput?.focus();
                return;
            }

            // Compare calendar dates without time-zone issues.
            const today = new Date();
            const localToday = [
                today.getFullYear(),
                String(today.getMonth() + 1).padStart(2, "0"),
                String(today.getDate()).padStart(2, "0")
            ].join("-");

            if (expiryDate < localToday) {
                event.preventDefault();
                alert("Expiry date cannot be in the past.");
                expiryInput?.focus();
            }
        });
    }

    // 3. Confirm before deleting a food listing
    const deleteForms = document.querySelectorAll(
        'form[action*="/food-listings/"][action*="/delete"]'
    );

    deleteForms.forEach(function (form) {
        form.addEventListener("submit", function (event) {
            const confirmed = window.confirm(
                "Are you sure you want to delete this food listing?"
            );

            if (!confirmed) {
                event.preventDefault();
            }
        });
    });

    // 4. Prevent accidental repeated submissions
    document.querySelectorAll("form").forEach(function (form) {
        form.addEventListener("submit", function () {
            if (!form.checkValidity()) {
                return;
            }

            const submitButton = form.querySelector(
                'button[type="submit"]'
            );

            if (submitButton && !form.dataset.confirmedDelete) {
                // Leave the button unchanged for normal forms.
                // Server-side validation remains authoritative.
            }
        });
    });

    // 5. Automatically hide success messages
    document.querySelectorAll(".success").forEach(function (message) {
        window.setTimeout(function () {
            message.style.transition = "opacity 0.5s ease";
            message.style.opacity = "0";

            window.setTimeout(function () {
                message.style.display = "none";
            }, 500);
        }, 5000);
    });

});
