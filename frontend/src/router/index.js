import {createRouter, createWebHistory} from 'vue-router'
import store from '../store'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/student/Login.vue'),
        meta: {requiresAuth: false}
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/student/Register.vue'),
        meta: {requiresAuth: false}
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/student/Dashboard.vue'),
        meta: {requiresAuth: true},
        children: [
            {
                path: '',
                redirect: '/dashboard/courses'
            },
            {
                path: 'courses',
                name: 'MyCourses',
                component: () => import('../views/student/Courses.vue')
            },
            {
                path: 'course/:id',
                name: 'CourseDetail',
                component: () => import('../views/student/CourseDetail.vue')
            },
            {
                path: 'question-banks',
                name: 'QuestionBanks',
                component: () => import('../views/student/QuestionBanks.vue')
            },
            {
                path: 'question-bank/:questionBankId',
                name: 'QuestionBankPractice',
                component: () => import('../views/student/QuestionBankPractice.vue')
            },
            {
                path: 'exams',
                name: 'Exams',
                component: () => import('../views/student/Exams.vue')
            },
            {
                path: 'exam/:questionBankId',
                name: 'Exam',
                component: () => import('../views/student/Exam.vue')
            },
            {
                path: 'exam-result/:questionBankId',
                name: 'ExamResult',
                component: () => import('../views/student/ExamResult.vue')
            },
            {
                path: 'exam-papers',
                name: 'ExamPapers',
                component: () => import('../views/student/ExamPapers.vue')
            },
            {
                path: 'exam-papers/:id',
                name: 'ExamPaperDetail',
                component: () => import('../views/student/ExamPaperDetail.vue')
            },
            {
                path: 'exam-papers/:id/exam',
                name: 'ExamPaperExam',
                component: () => import('../views/ExamPaperExam.vue')
            },
            {
                path: 'exam-papers/:id/results/:resultId',
                name: 'ExamPaperResult',
                component: () => import('../views/ExamPaperResult.vue')
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/student/Profile.vue')
            },
            {
                path: 'join',
                name: 'JoinInvitation',
                component: () => import('../views/student/JoinInvitation.vue')
            }
        ]
    },
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('../views/student/Admin.vue'),
        meta: {requiresAuth: true, requiresAdmin: true},
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
                path: 'exam-papers',
                name: 'AdminExamPapers',
                component: () => import('../views/admin/ExamPaperManagement.vue')
            },
            {
                path: 'exam-papers/:id',
                name: 'AdminExamPaperDetail',
                component: () => import('../views/admin/ExamPaperDetail.vue')
            },
            {
                path: 'exam-papers/:id/edit',
                name: 'AdminExamPaperEdit',
                component: () => import('../views/admin/ExamPaperEdit.vue')
            },
            {
                path: 'exam-papers/:id/questions',
                name: 'AdminExamPaperQuestions',
                component: () => import('../views/admin/ExamPaperQuestions.vue')
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
