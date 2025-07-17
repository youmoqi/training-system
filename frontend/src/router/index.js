import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard/courses'
      },
      {
        path: 'courses',
        name: 'MyCourses',
        component: () => import('../views/Courses.vue')
      },
      {
        path: 'course/:id',
        name: 'CourseDetail',
        component: () => import('../views/CourseDetail.vue')
      },
      {
        path: 'question-banks',
        name: 'QuestionBanks',
        component: () => import('../views/QuestionBanks.vue')
      },
      {
        path: 'question-bank/:questionBankId',
        name: 'QuestionBankPractice',
        component: () => import('../views/QuestionBankPractice.vue')
      },
      {
        path: 'exams',
        name: 'Exams',
        component: () => import('../views/Exams.vue')
      },
      {
        path: 'exam/:questionBankId',
        name: 'Exam',
        component: () => import('../views/Exam.vue')
      },
      {
        path: 'exam-result/:questionBankId',
        name: 'ExamResult',
        component: () => import('../views/ExamResult.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      },
      {
        path: 'join',
        name: 'JoinInvitation',
        component: () => import('../views/JoinInvitation.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/courses'
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('../views/admin/CourseManagement.vue')
      },
      {
        path: 'question-banks',
        name: 'AdminQuestionBanks',
        component: () => import('../views/admin/QuestionBankManagement.vue')
      },
      {
        path: 'question-banks/:questionBankId/questions',
        name: 'AdminQuestionManagement',
        component: () => import('../views/admin/QuestionManagement.vue')
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('../views/admin/Statistics.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/UserManagement.vue')
      },
      {
        path: 'invitations',
        name: 'AdminInvitations',
        component: () => import('../views/admin/InvitationManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = store.getters.isAuthenticated
  const userRole = store.getters.userRole

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !['SUPER_ADMIN', 'ADMIN'].includes(userRole)) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router 