<template>
  <div>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
      <div class="container">
        <a class="navbar-brand fw-semibold" href="#">
          St. Mary's Hospital
        </a>

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mainNav"
          aria-controls="mainNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div id="mainNav" class="collapse navbar-collapse">
          <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
            <li class="nav-item"><a class="nav-link" href="#services">Services</a></li>
            <li class="nav-item"><a class="nav-link" href="#team">Team</a></li>
            <li class="nav-item"><a class="nav-link" href="#contact">Contact</a></li>
            <li class="nav-item"><a class="nav-link" href="#Login">Login</a></li>
            <li class="nav-item ms-lg-2">
              <a class="btn btn-primary btn-sm px-3" href="#appointment">Request Appointment</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- Hero -->
    <header class="hero d-flex align-items-center">
      <div class="container">
        <div class="row align-items-center g-4">
          <div class="col-12 col-lg-7 text-white">
            <span class="badge rounded-pill bg-light text-dark mb-3">24/7 Emergency. Modern Diagnostics.</span>
            <h1 class="display-5 fw-semibold mb-3">
              Medicine with Calm, Time, and a Clear Plan
            </h1>
            <p class="lead mb-4 text-white-50">
              Here you'll find short distances, friendly reception, and an interdisciplinary team.
              From consultation to inpatient care.
            </p>

            <div class="d-flex flex-wrap gap-2">
              <a class="btn btn-primary btn-lg" href="#appointment">Request Appointment</a>
              <a class="btn btn-outline-light btn-lg" href="#services">View Services</a>
            </div>

            <div class="mt-4 d-flex flex-wrap gap-3 text-white-50 small">
              <div class="pill">Phone. 04321 123 456</div>
              <div class="pill">Address. Sample Street 10, 24534 Neumünster</div>
              <div class="pill">Office Hours. Mon to Fri</div>
            </div>
          </div>

          <div class="col-12 col-lg-5">
            <div class="glass card border-0 shadow-lg">
              <div class="card-body p-4">
                <h2 class="h5 fw-semibold mb-2">Quick Access</h2>
                <p class="text-muted mb-3">The most important actions without long searches.</p>

                <div class="d-grid gap-2">
                  <a class="btn btn-outline-primary" href="#appointment">Online Appointment</a>
                  <button class="btn btn-outline-secondary" type="button" @click="openEmergency">
                    Emergency Info
                  </button>
                  <button class="btn btn-outline-secondary" type="button" @click="openDirections">
                    Directions and Parking
                  </button>
                </div>

                <hr class="my-4" />

                <div class="d-flex align-items-center gap-3">
                  <div class="icon-bubble">
                    <span class="fw-bold">✓</span>
                  </div>
                  <div>
                    <div class="fw-semibold">Short Waiting Times</div>
                    <div class="text-muted small">Digital pre-registration and clear processes.</div>
                  </div>
                </div>

              </div>
            </div>
          </div>

        </div>
      </div>
    </header>

    <!-- Services -->
    <section id="services" class="py-5">
      <div class="container">
        <div class="row mb-4">
          <div class="col-lg-7">
            <h2 class="fw-semibold mb-2">Our Services</h2>
            <p class="text-muted mb-0">
              A modern clinic presence works best when it remains clear and reassuring.
            </p>
          </div>
        </div>

        <div class="row g-3">
          <div class="col-12 col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body">
                <div class="mini-icon mb-3">🩺</div>
                <h3 class="h6 fw-semibold">Outpatient Care</h3>
                <p class="text-muted small mb-0">Quick assessment. Clear next steps.</p>
              </div>
            </div>
          </div>

          <div class="col-12 col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body">
                <div class="mini-icon mb-3">🧪</div>
                <h3 class="h6 fw-semibold">Diagnostics</h3>
                <p class="text-muted small mb-0">Lab, imaging, results. Everything from one source.</p>
              </div>
            </div>
          </div>

          <div class="col-12 col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body">
                <div class="mini-icon mb-3">🏥</div>
                <h3 class="h6 fw-semibold">Inpatient Care</h3>
                <p class="text-muted small mb-0">Care with dedicated contact persons.</p>
              </div>
            </div>
          </div>

          <div class="col-12 col-md-6 col-lg-3">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body">
                <div class="mini-icon mb-3">💙</div>
                <h3 class="h6 fw-semibold">Aftercare</h3>
                <p class="text-muted small mb-0">Schedulable appointments. Clear recommendations.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Image Section -->
    <section class="py-5 bg-light">
      <div class="container">
        <div class="row g-4 align-items-center">
          <div class="col-12 col-lg-6">
            <div class="img-card shadow-sm" :style="`background-image: url('${imgReception}')`" />
          </div>
          <div class="col-12 col-lg-6">
            <h2 class="fw-semibold mb-2">Arrive. Orient. Feel Well Cared For.</h2>
            <p class="text-muted mb-3">
              A friendly welcome is often the most important moment. That's why we focus on clear paths,
              understandable information, and a calm design.
            </p>
            <div class="d-flex flex-wrap gap-2">
              <a class="btn btn-primary" href="#contact">Contact</a>
              <a class="btn btn-outline-secondary" href="#team">View Team</a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Team -->
    <section id="team" class="py-5">
      <div class="container">
        <div class="row mb-4">
          <div class="col-lg-7">
            <h2 class="fw-semibold mb-2">Our Team</h2>
            <p class="text-muted mb-0">
              Short texts work better here than long paragraphs. The rest can be conveyed through images.
            </p>
          </div>
        </div>

        <div class="row g-3">
          <div class="col-12 col-lg-7">
            <div class="img-card shadow-sm" :style="`background-image: url('${imgDoctorCall}')`" />
          </div>
          <div class="col-12 col-lg-5">
            <div class="card h-100 border-0 shadow-sm">
              <div class="card-body p-4">
                <h3 class="h5 fw-semibold mb-2">Interdisciplinary. Pragmatic. Approachable.</h3>
                <p class="text-muted">
                  From admission to therapy. We explain processes clearly and work systematically.
                </p>
                <ul class="text-muted small mb-0">
                  <li>Transparent treatment plans.</li>
                  <li>Digital documentation.</li>
                  <li>Coordination with primary care physicians.</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- Appointment -->
    <section id="appointment" class="py-5 cta">
      <div class="container">
        <div class="row align-items-center g-3">
          <div class="col-12 col-lg-8 text-white">
            <h2 class="fw-semibold mb-2">Request Appointment</h2>
            <p class="mb-0 text-white-50">
              Simple process. We'll get back to you promptly with a proposal.
            </p>
          </div>
          <div class="col-12 col-lg-4">
            <div class="d-grid gap-2">
              <button class="btn btn-light btn-lg" type="button" @click="startAppointment">
                Start Now
              </button>
              <a class="btn btn-outline-light" href="#contact">Or Call Us</a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Contact -->
    <footer id="contact" class="py-5 bg-dark text-white">
      <div class="container">
        <div class="row g-4">
          <div class="col-12 col-lg-5">
            <div class="fw-semibold mb-2">St. Mary's Hospital</div>
            <div class="text-white-50 small">
              Sample Street 10. 24534 Neumünster<br />
              Phone. 04321 123 456<br />
              Email. contact@st-mary.example
            </div>
          </div>

          <div class="col-6 col-lg-3">
            <div class="fw-semibold mb-2">Links</div>
            <div class="d-grid gap-1 small">
              <a class="link-light link-offset-2" href="#services">Services</a>
              <a class="link-light link-offset-2" href="#team">Team</a>
              <a class="link-light link-offset-2" href="#appointment">Appointment</a>
            </div>
          </div>

          <div class="col-6 col-lg-4">
            <div class="fw-semibold mb-2">Legal</div>
            <div class="text-white-50 small">
              Privacy Policy. Imprint. Accessibility.
            </div>
          </div>
        </div>

        <hr class="border-secondary my-4" />
        <div class="text-white-50 small">
          © {{ new Date().getFullYear() }} St. Mary's Hospital. All rights reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
const imgHospitalExterior =
  "https://images.unsplash.com/photo-1626315869436-d6781ba69d6e?auto=format&fit=crop&fm=jpg&q=60&w=3000"

const imgDoctorCall =
  "https://images.unsplash.com/photo-1758691461932-d0aa0ebf6b31?auto=format&fit=crop&fm=jpg&q=60&w=3000"

const imgReception =
  "https://images.unsplash.com/photo-1634047411861-59c2eaa2ec9a?auto=format&fit=crop&fm=jpg&q=60&w=3000"

function startAppointment() {
  alert("Placeholder. Here you would later open your form or routing.")
}

function openEmergency() {
  alert("Placeholder. Emergency phone numbers and information could go here.")
}

function openDirections() {
  alert("Placeholder. You could offer a map or route link here.")
}
</script>

<style scoped>
.hero {
  min-height: 78vh;
  background:
    linear-gradient(90deg, rgba(0, 0, 0, 0.72), rgba(0, 0, 0, 0.28)),
    url("https://images.unsplash.com/photo-1626315869436-d6781ba69d6e?auto=format&fit=crop&fm=jpg&q=60&w=3000");
  background-size: cover;
  background-position: center;
  padding: 4.5rem 0;
}

.pill {
  padding: 0.45rem 0.7rem;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 999px;
  backdrop-filter: blur(6px);
}

.glass {
  background: rgba(255, 255, 255, 0.92);
  border-radius: 18px;
}

.icon-bubble {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(13, 110, 253, 0.12);
  color: #0d6efd;
}

.mini-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: rgba(13, 110, 253, 0.08);
  font-size: 1.25rem;
}

.img-card {
  border-radius: 18px;
  height: 360px;
  background-size: cover;
  background-position: center;
}

.cta {
  background:
    linear-gradient(90deg, rgba(13, 110, 253, 0.95), rgba(13, 110, 253, 0.75)),
    url("https://images.unsplash.com/photo-1758691461932-d0aa0ebf6b31?auto=format&fit=crop&fm=jpg&q=60&w=3000");
  background-size: cover;
  background-position: center;
}
</style>
