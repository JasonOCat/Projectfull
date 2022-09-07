import {Popconfirm, Radio} from "antd";
import {deleteStudent} from "./client";
import {errorNotification, successNotification} from "./Notification";


function ActionButtons({text, student, fetchStudents}) {

    const removeStudent = (studentId, callback) => {
        deleteStudent(studentId).then(() => {
            successNotification("Student deleted", `Student with id ${studentId} was deleted`);
            callback();
        }).catch(err => {
            console.log(err.response)
            err.response.json().then(res => {
                console.log(res);
                errorNotification(
                    "There was an issue",
                    `${res.message} [${res.status}] [${res.error}]`
                )
            });
        });
    }



    return (<>
        <Radio.Group style={{borderColor: "none" }}>
            <Popconfirm
                title={`Are you sure to delete ${student.name}?`}
                onConfirm={() => removeStudent(student.id, fetchStudents)}
                okText="Yes"
                cancelText="No"
            >
                <Radio.Button value="small">Delete</Radio.Button>
            </Popconfirm>

            {/*            <Popconfirm
                    title="Are you sure to delete this task?"
                    onConfirm={confirm}
                    onCancel={cancel}
                    okText="Yes"
                    cancelText="No"
                >*/}
            <Radio.Button value="small">Edit</Radio.Button>
        </Radio.Group>
    </>)


}

export default ActionButtons;