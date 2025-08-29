export const getCategoryName = (category) => {
    const categoryMap = {
        'EXPLOSIVE_FIRST': '易制爆用户-首次培训',
        'EXPLOSIVE_CONTINUE': '易制爆用户-继续教育',
        'BLAST_THREE_FIRST': '爆破三大员-首次培训',
        'BLAST_THREE_CONTINUE': '爆破三大员-继续教育',
        'BLAST_TECH_FIRST': '爆破工程技术人员-首次培训',
        'BLAST_TECH_CONTINUE': '爆破工程技术人员-继续教育',
        'GENERAL': '通用'
    }
    return categoryMap[category] || category
}

export const getCategoryTagType = (category) => {
    const tagMap = {
        'EXPLOSIVE_FIRST': 'primary',
        'EXPLOSIVE_CONTINUE': 'success',
        'BLAST_THREE_FIRST': 'warning',
        'BLAST_THREE_CONTINUE': 'info',
        'BLAST_TECH_FIRST': 'danger',
        'BLAST_TECH_CONTINUE': 'warning',
        'GENERAL': 'info',
        'SUPER_ADMIN': 'purple',
    }
    return tagMap[category] || 'info'
}
