<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-5">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="card-title text-center mb-4">Register</h2>
            
            <div v-if="error" class="alert alert-danger" role="alert">
              {{ error }}
            </div>

            <form @submit.prevent="handleRegister">
              <div class="mb-3">
                <label for="name" class="form-label">Name</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="form.email"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password" 
                  v-model="form.password"
                  required
                  minlength="6"
                />
                <small class="form-text text-muted">At least 6 characters</small>
              </div>

              <div class="mb-3">
                <label for="role" class="form-label">Role</label>
                <select 
                  class="form-select" 
                  id="role" 
                  v-model="form.role"
                  required
                >
                  <option value="">Please select...</option>
                  <option value="PATIENT">Patient</option>
                  <option value="DOCTOR">Doctor</option>
                  <option value="NURSE">Nurse</option>
                  <option value="ADMIN">Administrator</option>
                </select>
              </div>

              <button 
                type="submit" 
                class="btn btn-primary w-100"
                :disabled="loading"
              >
                {{ loading ? 'Registering...' : 'Register' }}
              </button>
            </form>

            <div class="text-center mt-3">
              <p class="mb-0">Already have an account? <router-link to="/login">Sign in</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({
  name: '',
  email: '',
  password: '',
  role: ''
})
const error = ref('')
const loading = ref(false)

const handleRegister = async () => {
  error.value = ''
  loading.value = true

  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(form.value)
    })

    if (!response.ok) {
      const data = await response.json()
      throw new Error(data.message || 'Registrierung fehlgeschlagen')
    }

    const user = await response.json()
    console.log('Registrierung erfolgreich:', user)
    
    // Weiterleitung zum Login
    router.push('/login')
  } catch (err: any) {
    error.value = err.message || 'An error occurred'
  } finally {
    loading.value = false
  }
}
</script>
